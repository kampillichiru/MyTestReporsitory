package com.chiru.springboot.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.chiru.springboot.util.DmdLogger;

public abstract class DMDAbstractDao<PK extends Serializable, T> {
	static private final DmdLogger logger = DmdLogger.getLogger(
			DMDAbstractDao.class, DmdLogger.DMD_API);
	private final Class<T> persistentClass;
	@Autowired
	private DBConnectionDao dbConnectionDao;
	/*@Autowired
	@Qualifier("sessionFactoryPrimary")
	private SessionFactory sessionFactoryPrimary;
	@Autowired
	@Qualifier("sessionFactorySeconday")
	private SessionFactory sessionFactorySeconday;*/

	@SuppressWarnings("unchecked")
	public DMDAbstractDao() {
		this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass()
				.getGenericSuperclass()).getActualTypeArguments()[1];
	}
	
	protected Session getSession() {
		logger.debug("<==Hibernate is going to create==>");
		Session session=null;
		SessionFactory sessionFactoryPrimary = dbConnectionDao.getSessionFactory("sessionFactoryPrimary");
		if(sessionFactoryPrimary!=null)
		{
			session=sessionFactoryPrimary.openSession();
		}
		else
		{
			SessionFactory sessionFactorySeconday = dbConnectionDao.getSessionFactory("sessionFactorySeconday");
			session=sessionFactorySeconday.openSession();
		}
		
		return session;
		
	}

	public T getByKey(PK key) {
		return (T) getSession().get(persistentClass, key);
	}

	public void persist(T entity) {
		getSession().persist(entity);
	}

	public void delete(T entity) {
		logger.debug("<==Hibernate is going to delete Entity==>" + entity);
		getSession().delete(entity);
	}

	protected Criteria createEntityCriteria(Session session) {
		return session.createCriteria(persistentClass);
	}

	public void multiDBPersist(List<T> entityList) {

		List<SessionFactory> sessionFactoryList = dbConnectionDao
				.getSessionFactoryList();
		sessionFactoryList.removeAll(Collections.singleton(null));
		Set<T> entitySet = new HashSet<T>(entityList);
		for (SessionFactory sessionFactory : sessionFactoryList) {
			Session session = sessionFactory.openSession();
			if (session != null) {
				Transaction tx = session.beginTransaction();
				for (T entity : entitySet) {
					logger.debug("entity==>" + entity.toString());
					session.saveOrUpdate(entity);

				}
				tx.commit();
				session.flush();
				session.clear();
				session.close();

			}
		}
	}

	public void closeSession(Session session) {
		if (session != null) {
			try {

				session.close();
				logger.debug("Session closed " + session.isOpen());
			} catch (HibernateException ignored) {
				logger.error("Couldn't close Session" + ignored);
			}
		}

	}

	public boolean multiDBPersist(Session session, List<T> entityList) {
		try {
			Set<T> entitySet = new HashSet<T>(entityList);
			if (session != null) {
				for (T entity : entitySet) {
					logger.debug("entity==>" + entity.toString());
					session.saveOrUpdate(entity);

				}
				return true;

			}
		} catch (Exception e) {
			logger.error("Error occured while persisting list of objects", e);
			return false;

		}
		return false;
	}

}
