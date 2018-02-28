

@Repository("deRegisteredSubscribersDao")
public class DeRegisteredSubscribersDaoImpl implements DeRegisteredSubscribersDao {
	static private final DmdLogger logger = DmdLogger.getLogger(
			DeRegisteredSubscribersDaoImpl.class, DmdLogger.DMD_API);
	@Autowired
	private DBConnectionDao dbConnectionDao;

	public boolean subscriberDeRegister(Topic topic,String env) {
		CallableStatement stmt = null;

		try {
			logger.audit("subscriberDeRegister called: "+topic.toString());
			List<SessionFactory> sessionFactoryList=dbConnectionDao.getSessionFactoryList();
			String sql = "{call deregisterSubscriber (?,?,?,?)}";
			logger.debug("Environment: "+env+": No.of Connection: "+sessionFactoryList.size());
			for (SessionFactory sessionFactory : sessionFactoryList) {
				Session session = sessionFactory.openSession();
				logger.debug("Hibernate session created(subscriberDeRegister): " + session);
				Connection connection = DMDUtil.getConnection(session);
				logger.debug("Db connected URL(subscriberDeRegister): "+connection.getMetaData().getURL());
				stmt = connection.prepareCall(sql);
				stmt.setString(1, topic.getSubscriberUID());
				stmt.setString(2, topic.getFqdn());
				stmt.setString(3, topic.getTopicName());
				stmt.registerOutParameter(4, java.sql.Types.VARCHAR);
				stmt.executeQuery();
				String registerJson = stmt.getString(4);
				logger.audit("subscriberDeRegister().deregisterJson: " + registerJson);
				stmt.close();
				connection.close();
				session.flush();
				session.clear();
				session.close();
			}

		} catch (Exception e) {
			logger.error("Subscriber is not De-Registered: ",e);
			return false;
		}

		return true;
	}

}
