

public abstract class DMDAbstractDao<PK extends Serializable, T> {
	static private final DmdLogger logger = DmdLogger.getLogger(
			DMDAbstractDao.class, DmdLogger.DMD_API);
	private final Class<T> persistentClass;
	@Autowired
	private DBConnectionDao dbConnectionDao;

	@SuppressWarnings("unchecked")
	public DMDAbstractDao() {
		this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass()
				.getGenericSuperclass()).getActualTypeArguments()[1];
	}

	@Autowired
	@Qualifier("sessionFactoryPrimary")
	private SessionFactory sessionFactoryPrimary;

	protected Session getSession() {
		logger.debug("<==Hibernate is going to create==>");
		return sessionFactoryPrimary.openSession();
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
