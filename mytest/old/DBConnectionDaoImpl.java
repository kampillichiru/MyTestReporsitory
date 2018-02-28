

@Component("dbConnectionDao")
public class DBConnectionDaoImpl implements DBConnectionDao {
	static private final DmdLogger logger = DmdLogger.getLogger(
			DBConnectionDaoImpl.class, DmdLogger.DMD_API);
	@Autowired
	@Qualifier("sessionFactoryPrimary")
	private SessionFactory sessionFactoryPrimary;
	@Autowired
	@Qualifier("sessionFactorySeconday")
	private SessionFactory sessionFactorySeconday;
	

	@Override
	public List<SessionFactory> getSessionFactoryList() {
		List<SessionFactory> sessionFactoryList = new ArrayList<SessionFactory>();
		if (sessionFactoryPrimary != null) {
			logger.audit("PrimaryDB added to the list: "+sessionFactoryPrimary);
			sessionFactoryList.add(sessionFactoryPrimary);
		}
		if (sessionFactorySeconday != null) {
			logger.audit("SecondaryDB added to the list: "+sessionFactorySeconday);
			sessionFactoryList.add(sessionFactorySeconday);
		}
		return sessionFactoryList;

	}

}
