/*
 * Copyright 2009 University of Zurich, Switzerland
 * Copyright 2013 Quinten Soetens - Adapted from org.evolizer.core.hibernate
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

import hibernate.HibernateException;
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
     * Instantiates a new session.
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
    public int query(String hqlQuery) throws Exception {
    	assertSessionIsOpen();
    	
    	Query query = fHibernateSession.createQuery(hqlQuery);
    	
    	List<Long> l = query.list();
    	
    	
		return l.get(0).intValue();
	}
    
    

    /**
     * {@inheritDoc}
     */
    public void startTransaction() throws Exception {
        assertSessionIsOpen();
        assertTransactionIsNotActive();

        fTransaction = fHibernateSession.beginTransaction();
        
        ensureTransactionIsActive();
    }

    private void ensureTransactionIsActive() throws Exception {
        if (fTransaction == null || !fTransaction.isActive()) {
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
            Exception ex = new HibernateException("Non unique result for uniqueResult query");
            throw ex;
        }
    }

    private void assertTransactionIsNotActive() throws Exception {
        if (fTransaction != null && fTransaction.isActive()) {
            Exception ex = new HibernateException("A Transaction is already active.");
            throw ex;
        }
    }

    private void assertSessionIsOpen() throws Exception {
        if (!isOpen()) {
            Exception ex = new HibernateException("Session is not open.");
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

	public void clearDatabase() {
		try{
			fHibernateSession.createSQLQuery("truncate table \"CHANGESTRUCTDEPS\"").executeUpdate();
			fHibernateSession.createSQLQuery("truncate table \"INVOCATIONCANDIDATES\"").executeUpdate();
			fHibernateSession.createSQLQuery("truncate table \"CHANGE\"").executeUpdate();
			fHibernateSession.createSQLQuery("truncate table \"SUBJECT\"").executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    
}