

@Component("dbConnectionDao")
public class DBConnectionDaoImpl implements DBConnectionDao {
	static private final DmdLogger logger = DmdLogger.getLogger(
			DBConnectionDaoImpl.class, DmdLogger.DMD_API);
	@Autowired
	private ApplicationContext context;

	@Override
	public List<SessionFactory> getSessionFactoryList() {
		List<SessionFactory> sessionFactoryList = new ArrayList<SessionFactory>();
		
		SessionFactory sessionFactoryPrimary = getSessionFactory("sessionFactoryPrimary");
		if (sessionFactoryPrimary != null) {
			logger.audit("PrimaryDB added to the list: " + sessionFactoryPrimary);
			sessionFactoryList.add(sessionFactoryPrimary);
		}
		SessionFactory sessionFactorySercondary=getSessionFactory("sessionFactorySercondary");
		if (sessionFactorySercondary != null) {
			logger.audit("SecondaryDB added to the list: " + sessionFactorySercondary);
			sessionFactoryList.add(sessionFactorySercondary);
		}
		return sessionFactoryList;

	}


	@Override
	public SessionFactory getSessionFactory(
			String sessionFactoryName) {
		SessionFactory sessionFactoryPrimary=null;
		try {
			sessionFactoryPrimary = context.getBean(sessionFactoryName,SessionFactory.class);
		} catch (BeansException e) {
			e.printStackTrace();
		}
		return sessionFactoryPrimary;
	}

}
