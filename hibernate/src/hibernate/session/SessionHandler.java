/*
 * Copyright 2009 University of Zurich, Switzerland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hibernate.session;

import hibernate.DBProperties;
import hibernate.HibernatePlugin;
import hibernate.model.api.IModelEntity;
import hibernate.model.api.IModelProvider;
import hibernate.session.api.ISession;
import hibernate.session.internal.SessionImpl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

/**
 * Handling of multiple Hibernate sessions using the ThreadLocal sessions. Multiple Hibernate Sessions are managed by
 * keeping a map of database url to session factory. There are two usage patterns to obtain a session.
 * <ol>
 * <li>Via the Evolizer project properties for Eclipse Evolizer plugins. Using the properties, the current session of
 * the corresponding session factory is obtained. No precedent initialization of the session factory is needed.</li>
 * <li>Manually by providing the session database url as string. In this case the session factory needs to be
 * initialized before.</li>
 * </ol>
 * Some suggestions: According to some articles on the Internet it is suggested to use one session per view. It does not
 * make sense to open and close sessions for each database access. You simply will get dozens of Hibernate exceptions,
 * e.g., because of lazy loading. Therefore, the session should be opened when the view is created and closed when the
 * view is closed.
 * 
 * @author pinzger
 */
public final class SessionHandler {
     /**
     * Default Hibernate dialect
     */
    private static final String DEFAULT_DIALECT = "org.hibernate.dialect.MySQLDialect";
    /**
     * Default Hibernate database driver
     */
    private static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";

    /**
     * Singleton of EvolizerSessionHandler
     */
    private static SessionHandler sSessionHandler;

    /**
     * Map holding the configured Hibernate session factories. The Url of the database is used as key.
     */
    private Hashtable<String, SessionFactory> fSessionFactoryMap = new Hashtable<String, SessionFactory>();

    /**
     * Map holding Hibernate sessions.
     */
    private Hashtable<String, ISession> fSessionMap = new Hashtable<String, ISession>();

    /**
     * Hidden default constructor.
     */
    private SessionHandler() {}

    /**
     * Factory method creating the single instance of {@link SessionHandler}.
     * 
     * @return The single instance of the current {@link SessionHandler}
     */
    public static SessionHandler getHandler() {
        if (sSessionHandler == null) {
            sSessionHandler = new SessionHandler();
        }
        return sSessionHandler;
    }

    /**
     * Obtains the current session for the given the Url of the database. Keep in mind that ThreadLocal has to be
     * activated: current_session_context_class = "thread" This method can only be used after the Hibernate session has
     * been initialized.
     * 
     * @param dbUrl
     *            URL of the database in the form of <code>dbHost/dbName</code>, e.g.,
     *            <code>mysql://localhost:3306/evolizer_test</code>
     * @return the current Hibernate session.
     * @throws Exception
     *             if the {@link ISession} could not been initialized/obtained
     */
    public ISession getCurrentSession(String dbUrl) throws Exception {
        ISession session = null;

        if (fSessionMap.containsKey(dbUrl) && fSessionMap.get(dbUrl).isOpen()) {
            session = fSessionMap.get(dbUrl);
        } else {
            if (getSessionFactory(dbUrl) != null) {
                Session hibernateSession = getSessionFactory(dbUrl).openSession();
                session = new SessionImpl(hibernateSession);
                fSessionMap.put(dbUrl, session);
            } else {
                throw new Exception("Evolizer session factory for '" + dbUrl + "' has not been initialized.");
            }

        }
        return session;
    }

    /**
     * Obtains the Hibernate configuration from the project properties, initializes the Hibernate session factory and
     * returns the current Hibernate session.
     * 
     * @param project
     *            the Eclipse project containing Evolizer properties.
     * @return the current Hibernate session.
     * @throws Exception
     *             if the {@link ISession} could not been initialized/obtained
     */
    public ISession getCurrentSession(IProject project) throws Exception {
        ISession session = null;

        try {
            String dbUrl = DBProperties.dbUrl;

            if (getSessionFactory(dbUrl) == null) {
                String dbUser = DBProperties.dbUser;
                String dbPassword = DBProperties.dbPassword;
                initSessionFactory(dbUrl, dbUser, dbPassword);
            }

            session = getCurrentSession(dbUrl);
        } catch (CoreException e) {
        }

        return session;
    }

    /**
     * Initializes the Hibernate session factory with the given dbUrl, dbUser, and dbPassword. Each newly initializes
     * session factory is remembered in the session factory map.
     * 
     * @param dbUrl
     *            Url of the dabatase.
     * @param dbUser
     *            User of the database.
     * @param dbPassword
     *            Password of the user.
     * @throws Exception
     *             if the {@link ISession} could not been initialized/obtained
     */
    public void initSessionFactory(String dbUrl, String dbUser, String dbPassword) throws Exception {

        if (!fSessionFactoryMap.containsKey(dbUrl)) {
            AnnotationConfiguration configuration = configureDataBaseConnection(dbUrl, "", "", dbUser, dbPassword);
            fSessionFactoryMap.put(dbUrl, configuration.buildSessionFactory());
        }
    }

    /**
     * Cleanup all open Hibernate sessions. Should be used when an application is closed.
     * @throws Exception 
     */
    public void cleanupHibernateSessions() throws Exception {
        for (String dbUrl : fSessionMap.keySet()) {
            if (fSessionMap.get(dbUrl).isOpen()) {
                fSessionMap.get(dbUrl).close();
            }
        }

        cleanupHibernateSessionFactories();
    }

    /**
     * Cleanup open Hibernate session factories.
     */
    private void cleanupHibernateSessionFactories() {
        for (String dbUrl : fSessionFactoryMap.keySet()) {
            fSessionFactoryMap.get(dbUrl).close();
        }
    }

    /**
     * Helper function using the dbUrl to obtain the Hibernate session factory from the map. If the session factory has
     * not been initializes before, <code>null</code> is returned.
     * 
     * @param dbUrl
     *            Url (key) of the database.
     * @return the corresponding Hibernate session factory.
     * @throws Exception
     *             if the {@link ISession} could not been obtained
     */
    private SessionFactory getSessionFactory(String dbUrl) throws Exception {
        SessionFactory sessionFactory = null;

        if (fSessionFactoryMap.containsKey(dbUrl)) {
            sessionFactory = fSessionFactoryMap.get(dbUrl);
        }

        return sessionFactory;
    }

    /**
     * Creates the schema for the given project.
     * 
     * @param project
     *            the project
     * @throws Exception
     *             the evolizer exception
     */
    public void createSchema(IProject project) throws Exception {
        SchemaExport exporter = new SchemaExport(configureDataBaseConnection(project));
        exporter.create(false, true);
    }

    /**
     * Update schema.
     * 
     * @param project
     *            the project
     * @throws Exception
     *             if this method fails. Reasons include:
     *             <ul>
     *             <li>The project does not exist.</li>
     *             <li>The project is not local.</li>
     *             <li>The project is a project that is not open.</li>
     *             </ul>
     */
    public void updateSchema(IProject project) throws Exception {
        SchemaUpdate updater = new SchemaUpdate(configureDataBaseConnection(project));
        updater.execute(false, true);
    }

    /**
     * Drop schema for the given project.
     * 
     * @param project
     *            the project
     * @throws Exception
     *             if this method fails. Reasons include:
     *             <ul>
     *             <li>The project does not exist.</li>
     *             <li>The project is not local.</li>
     *             <li>The project is a project that is not open.</li>
     *             </ul>
     */
    public void dropSchema(IProject project) throws Exception {
        SchemaExport exporter = new SchemaExport(configureDataBaseConnection(project));
        exporter.drop(false, true);
    }

    /**
     * Creates the database schema based on the o/r mappings (e.g. the Hibernate/ejb3-annotations). Can only be executed
     * when session is closed.
     * 
     * @param dbUrl
     *            the db url
     * @param dbDialect
     *            the db dialect
     * @param dbDriverName
     *            the db driver name
     * @param dbUser
     *            the db user
     * @param dbPassword
     *            the db password
     * @throws Exception
     *             if error occurs during o/r mapping
     */
    public void createSchema(String dbUrl, String dbDialect, String dbDriverName, String dbUser, String dbPassword)
            throws Exception {
        SchemaExport exporter =
                new SchemaExport(configureDataBaseConnection(dbUrl, dbDialect, dbDriverName, dbUser, dbPassword));
        exporter.create(false, true);
    }

    /**
     * Updates a database schema based on the o/r mappings (e.g. the Hibernate/ejb3-annotations). Can only be executed
     * when session is closed.
     * 
     * @param dbUrl
     *            the db url
     * @param dbDialect
     *            the db dialect
     * @param dbDriverName
     *            the db driver name
     * @param dbUser
     *            the db user
     * @param dbPassword
     *            the db password
     * @throws Exception
     *             the evolizer exception
     */
    public void updateSchema(String dbUrl, String dbDialect, String dbDriverName, String dbUser, String dbPassword)
            throws Exception {
        SchemaUpdate updater =
                new SchemaUpdate(configureDataBaseConnection(dbUrl, dbDialect, dbDriverName, dbUser, dbPassword));
        updater.execute(false, true);
    }

    /**
     * Drops the database schema. Can only be executed when session is closed.
     * 
     * @param dbUrl
     *            the db url
     * @param dbDialect
     *            the db dialect
     * @param dbDriverName
     *            the db driver name
     * @param dbUser
     *            the db user
     * @param dbPassword
     *            the db password
     * @throws Exception
     *             when the mapping could not be generated.
     */
    public void dropSchema(String dbUrl, String dbDialect, String dbDriverName, String dbUser, String dbPassword)
            throws Exception {
        SchemaExport exporter =
                new SchemaExport(configureDataBaseConnection(dbUrl, dbDialect, dbDriverName, dbUser, dbPassword));
        exporter.drop(false, true);
    }

    /**
     * Creates the Hinbernate configuration based on the given parameters and the list of annotated classes.
     * 
     * @param dbUrl
     *            database host (e.g. <code>mysql://localhost:3306/evolizer_test</code>)
     * @param dbDialect
     *            database dialect (e.g. <code>org.hibernate.dialect.MySQLDialect</code>)
     * @param dbDriverName
     *            jdbc-compliant database driver (e.g. <code>com.mysql.jdbc.Driver</code>)
     * @param dbUser
     *            database username
     * @param dbPasswd
     *            database password for dbUser
     * @return the Hibernate configuration.
     * @throws Exception
     *             when the mapping could not be generated.
     */
    private AnnotationConfiguration configureDataBaseConnection(
            String dbUrl,
            String dbDialect,
            String dbDriverName,
            String dbUser,
            String dbPassword) throws Exception {

        AnnotationConfiguration configuration = new AnnotationConfiguration();

        try {
            configuration.setProperty("hibernate.connection.url", "jdbc:" + dbUrl);
            configuration.setProperty("hibernate.connection.username", dbUser);
            configuration.setProperty("hibernate.connection.password", dbPassword);

            if (dbDialect.equals("")) {
                configuration.setProperty("hibernate.dialect", DEFAULT_DIALECT);
            } else {
                configuration.setProperty("hibernate.dialect", dbDialect);
            }
            if (dbDriverName.equals("")) {
                configuration.setProperty("hibernate.connection.driver_class", DEFAULT_DRIVER);
            } else {
                configuration.setProperty("hibernate.connection.driver_class", dbDriverName);
            }
            configuration.setProperty("hibernate.jdbc.batch_size", "25");
            configuration.setProperty("hibernate.connection.pool_size", "2");
            configuration.setProperty("hibernate.cache.use_second_level_cache", "false");
            configuration.setProperty("hibernate.current_session_context_class", "thread");
            // configuration.setProperty("hibernate.current_session_context_class", "managed");

            // fHibernateAnnotationConfig.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");

            for (Class<?> annotatedClass : gatherModels()) {
                configuration.addAnnotatedClass(annotatedClass);

                // sfLogger.debug("Added annotated class '" + annotatedClass.getCanonicalName() +
                // "' to configuration.");
            }
        } catch (MappingException e) {
            throw new Exception(e);
        }

        return configuration;
    }

    /**
     * Creates the Hibernate configuration for the given Eclipse project. Hibernate properties are obtained from the
     * project's Evolizer properties.
     * 
     * @param project
     *            the Eclipse project holding the Evolizer properties.
     * @return the Hinberate configuration.
     * @throws Exception
     *             if this method fails. Reasons include:
     *             <ul>
     *             <li>The project does not exist.</li>
     *             <li>The project is not local.</li>
     *             <li>The project is a project that is not open.</li>
     *             </ul>
     */
    private AnnotationConfiguration configureDataBaseConnection(IProject project) throws Exception {
        AnnotationConfiguration configuration = null;

        try {
            String dbUrl = DBProperties.dbUrl;
            String dbUser = DBProperties.dbUser;
            String dbPassword = DBProperties.dbPassword;

            configuration = configureDataBaseConnection(dbUrl, "", "", dbUser, dbPassword);

        } catch (CoreException e) {
            throw new Exception(e);
        }

        return configuration;
    }

    /**
     * Queries all model providers and returns ejb3-annotated classes.
     * 
     * @return A list containing classes that are annotated with ejb3-tags for mapping.
     * @throws Exception 
     */
    private List<Class<?>> gatherModels() throws Exception {
        List<Class<?>> annotatedClasses = new ArrayList<Class<?>>();

        // Iterate over all extensions and gather classes that are hibernate annotated
        IExtension[] extensions =
                Platform.getExtensionRegistry().getExtensionPoint(HibernatePlugin.PLUGIN_ID, "modelProvider")
                        .getExtensions();
        for (IExtension element : extensions) {
            IConfigurationElement[] configElements = element.getConfigurationElements();
            for (IConfigurationElement configElement : configElements) {
                try {
                    IModelProvider provider =
                            (IModelProvider) configElement.createExecutableExtension("class");
                    // Throws CoreException if executable could not be created.
                    Class<?>[] classes = provider.getAnnotatedClasses();
                    for (Class<?> clazz : classes) {
                        if (isModelEntity(clazz)) {
                            annotatedClasses.add(clazz);
                        } else {
                            throw new Exception(clazz.getSimpleName()
                                    + " does not implement IEvolizerModelEntity.");
                        }
                    }

                } catch (CoreException exception) {
                    String message =
                            "Could not create executable extension from " + configElement.getContributor() + ". "
                                    + exception.getMessage();


                    throw new Exception("Error while initializing log properties.", exception);
                }
            }
        }

        return annotatedClasses;
    }

    private boolean isModelEntity(Class<?> annotatedClass) {
        Class<?>[] interfaces = annotatedClass.getInterfaces();
        for (Class<?> interf : interfaces) {
            if (interf.equals(IModelEntity.class)) {
                return true;
            }
        }

        Class<?> superClass = annotatedClass.getSuperclass();
        if (superClass.equals(Object.class)) {
            return false;
        } else {
            return isModelEntity(superClass);
        }
    }
}
