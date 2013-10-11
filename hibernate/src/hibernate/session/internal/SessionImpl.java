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
package hibernate.session.internal;

import hibernate.HibernatePlugin;
import hibernate.session.SessionHandler;
import hibernate.session.api.ISession;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * This class wraps a Hibernate session and provides behaviour for making objects of Hibernate/ejb3-annotated classes
 * persistent.
 * 
 * @author wuersch
 */
public class SessionImpl implements ISession {

    /**
     * The Hibernate session.
     */
    private Session fHibernateSession;
    /**
     * The configuration of the Hibernate session.
     */
    private AnnotationConfiguration fHibernateAnnotationConfig;
    /**
     * Whenever a transaction is open, its reference is stored here.
     */
    private Transaction fTransaction;

    /**
     * Constructor. Not intended to be called by clients directly. Use
     * {@link SessionFactory#getEvolizerSession(String, String, String, String, String)} instead.
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
     * @throws Exception 
     */
    public SessionImpl(String dbUrl, String dbDialect, String dbDriverName, String dbUser, String dbPasswd) throws Exception {

        configureDataBaseConnection(dbUrl, dbDialect, dbDriverName, dbUser, dbPasswd);
    }

    /**
     * Instantiates a new Evolizer session.
     * 
     * @param session
     *            the hibernate session
     */
    public SessionImpl(Session session) {
        fHibernateSession = session;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isOpen() {
        return (fHibernateSession != null) && fHibernateSession.isOpen();
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws Exception {
        assertSessionIsOpen();

        flush();
        fHibernateSession.close();
        fHibernateSession = null;
    }

    /**
     * {@inheritDoc}
     */
    public void flush() throws Exception {
        assertSessionIsOpen();

        fHibernateSession.flush();
    }

    /**
     * {@inheritDoc}
     * @throws Exception 
     */
    public Object merge(Object object) throws Exception {
        assertSessionIsOpen();

        return fHibernateSession.merge(object);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() throws Exception {
        assertSessionIsOpen();

        fHibernateSession.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void saveObject(Object saveableObject) throws Exception {
        assertSessionIsOpen();

        fHibernateSession.save(saveableObject);
    }

    /**
     * {@inheritDoc}
     */
    public void saveOrUpdate(Object object) throws Exception {
        assertSessionIsOpen();

        fHibernateSession.saveOrUpdate(object);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(Object object) throws Exception {
        assertSessionIsOpen();

        fHibernateSession.delete(object);
    }

    /**
     * {@inheritDoc}
     */
    public void update(Object updateableObject) throws Exception {
        assertSessionIsOpen();

        fHibernateSession.update(updateableObject);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> query(String hqlQuery, Class<T> type) throws Exception {
        assertSessionIsOpen();

        Query query = fHibernateSession.createQuery(hqlQuery);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> query(String hqlQuery, Class<T> type, int maxResults) throws Exception {
        assertSessionIsOpen();

        Query query = fHibernateSession.createQuery(hqlQuery);
        query.setMaxResults(maxResults);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    public void startTransaction() throws Exception {
        assertSessionIsOpen();
        assertTransactionIsNotActive();

        fTransaction = fHibernateSession.beginTransaction();
    }

    private void ensureTransactionIsActive() throws Exception {
        if (fTransaction == null) {
            Exception ex = new Exception("No Transaction is active.");

            throw ex;
        }
    }

    /**
     * {@inheritDoc}
     * @throws Exception 
     */
    public void endTransaction() throws Exception {
        assertSessionIsOpen();
        ensureTransactionIsActive();

        fTransaction.commit();
        fTransaction = null;
    }

    /**
     * {@inheritDoc}
     * @throws Exception 
     */
    public void rollbackTransaction() throws Exception {
        assertSessionIsOpen();
        ensureTransactionIsActive();

        fTransaction.rollback();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T uniqueResult(String hqlQuery, Class<T> type) throws Exception {
        assertSessionIsOpen();

        Query query = fHibernateSession.createQuery(hqlQuery);

        try {
            return (T) query.uniqueResult();
        } catch (NonUniqueResultException e) {
            Exception ex = new Exception("Non unique result for uniqueResult query");
            throw ex;
        }
    }

    /**
     * Creates and stores the configuration for the Hibernate-session based on the passed parameters. Furthermore, it
     * queries all <code>org.evolizer.hibernate.modelProvider</code> extensions for annotated classes.
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
     * @throws Exception 
     * @deprecated Method has been moved to
     *             {@link SessionHandler#configureDataBaseConnection(String, String, String, String, String)}
     */
    @Deprecated
    private void configureDataBaseConnection(
            String dbUrl,
            String dbDialect,
            String dbDriverName,
            String dbUser,
            String dbPasswd) throws Exception {

        fHibernateAnnotationConfig = new AnnotationConfiguration();

        fHibernateAnnotationConfig.setProperty("hibernate.connection.url", "jdbc:" + dbUrl);
        fHibernateAnnotationConfig.setProperty("hibernate.connection.username", dbUser);
        fHibernateAnnotationConfig.setProperty("hibernate.connection.password", dbPasswd);
        fHibernateAnnotationConfig.setProperty("hibernate.dialect", dbDialect);
        fHibernateAnnotationConfig.setProperty("hibernate.connection.driver_class", dbDriverName);

        fHibernateAnnotationConfig.setProperty("hibernate.jdbc.batch_size", "25");
        fHibernateAnnotationConfig.setProperty("hibernate.cache.use_second_level_cache", "false");

        // fHibernateAnnotationConfig.setProperty("hibernate.show_sql", "true");

        List<Class<?>> annotatedClasses = HibernatePlugin.getDefault().gatherModels();
        for (Class<?> annotatedClass : annotatedClasses) {
            fHibernateAnnotationConfig.addAnnotatedClass(annotatedClass);

        }
    }

    private void assertTransactionIsNotActive() throws Exception {
        if (fTransaction != null) {
            Exception ex = new Exception("A Transaction is already active.");
            throw ex;
        }
    }

    private void assertSessionIsOpen() throws Exception {
        if (!isOpen()) {
            Exception ex = new Exception("Session is not open.");
            throw ex;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T load(Class<T> clazz, Long id) throws Exception {
        assertSessionIsOpen();

        return (T) fHibernateSession.load(clazz, id);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, Serializable id) throws Exception {
        assertSessionIsOpen();

        return (T) fHibernateSession.get(clazz, id);
    }

    /**
     * {@inheritDoc}
     */
    public Session getHibernateSession() {
        return fHibernateSession;
    }
}
