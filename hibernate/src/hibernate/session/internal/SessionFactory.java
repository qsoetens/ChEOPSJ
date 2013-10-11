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

import hibernate.DBProperties;
import hibernate.HibernatePlugin;
import hibernate.session.api.ISession;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * A factory for creating {@link SessionFactory} objects.
 * 
 * @author wuersch
 */
public final class SessionFactory {
    
    private SessionFactory() {}

    /**
     * Returns an implementation of {@link ISession} configured using the persistent properties of the passed
     * {@link IProject}.
     * 
     * @param project
     *            to gain database configuration of
     * @return an implementation of {@link SessionFactory}
     * @throws EvolizerException
     *             if session could not be created because preference store of project is not accessible. Reasons
     *             include
     *             <ul>
     *             <li>Project does not exist.</li>
     *             <li>Project is not local.</li>
     *             <li>Project is not open.</li>
     *             </ul>
     */
    public static ISession getEvolizerSession(IProject project) throws Exception {

        try {
            String url = DBProperties.dbUrl;
            String user = DBProperties.dbUser;
            String password = DBProperties.dbPassword;
            String dialect = DBProperties.dbDialect;
            String driver = DBProperties.dbDriverName;

            return SessionFactory.getEvolizerSession(
                    url,
                    dialect,
                    driver,
                    user,
                    password);

        } catch (CoreException e) {
            String message = "Error while fetching persistent properties from project '" + project.getName() + "'."
                    + e.getMessage();
            throw new Exception(message);
        }
    }

    /**
     * Returns an implementation of {@link ISession} configured with the passed params.
     * 
     * @param dbUrl
     *            database host (e.g. <code>mysql://localhost:3306/evolizer_test</code>)
     * @param dbDialect
     *            database dialect (e.g. o<code>rg.hibernate.dialect.MySQLDialect</code>)
     * @param dbDriverName
     *            jdbc-compliant database driver (e.g. <code>com.mysql.jdbc.Driver</code>)
     * @param dbUser
     *            database username
     * @param dbPasswd
     *            database password for dbUser
     * @return an implementation of {@link SessionFactory}
     * @throws Exception 
     */
    public static ISession getEvolizerSession(
            String dbUrl,
            String dbDialect,
            String dbDriverName,
            String dbUser,
            String dbPasswd) throws Exception {

        return new SessionImpl(dbUrl, dbDialect, dbDriverName, dbUser, dbPasswd);
    }
}
