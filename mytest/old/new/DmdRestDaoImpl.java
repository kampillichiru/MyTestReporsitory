

@Repository
public class DmdRestDaoImpl implements DmdRestDao {
	static private final DmdLogger logger = DmdLogger.getLogger(DmdRestDaoImpl.class, DmdLogger.DMD_API);
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private Environment environment;
	@Autowired
	private DMDEnv dmdEnv;
	@Autowired
	private DBConnectionDao dbConnectionDao;

	@Override
	public String publisherRegister(String topic, String fqdn, String ipAddress) {

		String registerOutput = null;
		Session session =null;
		try {

			logger.metrics("publisherRegister topic: " + topic + ", fqdn: " + fqdn);

			 session = getSession();
			 
			ProcedureCall call = session.createStoredProcedureCall("registerPublisher");

			call.registerParameter(1, String.class, ParameterMode.IN).bindValue(fqdn);
			call.registerParameter(2, String.class, ParameterMode.IN).bindValue(topic);
			call.registerParameter(3, String.class, ParameterMode.IN).bindValue(ipAddress);
			call.registerParameter(4, String.class, ParameterMode.INOUT);

			registerOutput = (String) call.getOutputs().getOutputParameterValue(4);

			logger.metrics("publisherRegister().registerOutput: " + registerOutput);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Publisher Register is not done: " + e.getMessage());
		}
		finally {
			closeSession(session);
		}
		return registerOutput;
	}

	private Session getSession() {
		Session session;
		SessionFactory sessionFactoryPrimary = dbConnectionDao.getSessionFactory("sessionFactoryPrimary");
		String env = environment.getProperty("spring.profiles.active");
		logger.metrics("Environment: " + env);
		String hostName = environment.getProperty(env.toLowerCase() + ".primary");
		logger.metrics("hostName: " + hostName);
		System.out.println("<==== Testing for new deployment====>");
		if (sessionFactoryPrimary != null && DMDUtil.getLiveHostName(hostName, 3306)) {
			session = sessionFactoryPrimary.getCurrentSession();
		} else {
			//dmdEnv.setPrimaryDBUp(false);
			SessionFactory sessionFactorySecondary = dbConnectionDao.getSessionFactory("sessionFactorySercondary");
			session = sessionFactorySecondary.getCurrentSession();
		}
		
		
		/*ConnectionInfo connectionInfo = new ConnectionInfo();
	     session.doWork(connectionInfo);
	     logger.audit("DataBaseProductName : "+connectionInfo.getDataBaseProductName());
	     logger.audit("DataBaseUrl : "+connectionInfo.getDataBaseUrl());
	     logger.audit("DriverName : "+connectionInfo.getDriverName());
	     logger.audit("Username : "+connectionInfo.getUsername());*/
	     
		
		return session;
	}

	@Override
	public String subscriberRegister(Topic topic) {
		String registerOutput = null;
		Session session =null;
		try {
			logger.metrics("subscriberRegister topic: " + topic.toString());

			 session = getSession();
			ProcedureCall call = session.createStoredProcedureCall("registerSubscriber");

			call.registerParameter(1, String.class, ParameterMode.IN).bindValue(topic.getSubscriberUID());
			call.registerParameter(2, String.class, ParameterMode.IN).bindValue(topic.getFqdn());
			call.registerParameter(3, String.class, ParameterMode.IN).bindValue(topic.getTopicName());
			call.registerParameter(4, Integer.class, ParameterMode.IN)
					.bindValue(Integer.parseInt(topic.getDeliveryGuarantee()));
			call.registerParameter(5, Integer.class, ParameterMode.IN)
					.bindValue(Integer.parseInt(topic.getDurableSubscriptionFlag()));
			call.registerParameter(6, String.class, ParameterMode.IN).bindValue(topic.getIpAddress());
			call.registerParameter(7, String.class, ParameterMode.INOUT);

			registerOutput = (String) call.getOutputs().getOutputParameterValue(7);

			logger.metrics("subscriberRegister().registerOutput: " + registerOutput);
		} catch (Exception e) {
			logger.error("Subscriber Register is not done: " + e.getMessage());
		}
		finally
		{
			closeSession(session);
		}
		return registerOutput;
	}

	@Override
	public void publisherDeRegister(String topic, String fqdn, String ipAddress) {

		String deregisterOutput = null;
		Session session =null;
		try {
			logger.metrics("publisherDeRegister topic: " + topic + "," + fqdn);

			 session = getSession();
			ProcedureCall call = session.createStoredProcedureCall("deregisterPublisher");

			call.registerParameter(1, String.class, ParameterMode.IN).bindValue(fqdn);
			call.registerParameter(2, String.class, ParameterMode.IN).bindValue(topic);
			call.registerParameter(3, String.class, ParameterMode.IN).bindValue(ipAddress);
			call.registerParameter(4, String.class, ParameterMode.INOUT);

			deregisterOutput = (String) call.getOutputs().getOutputParameterValue(4);
			logger.metrics("publisherDeRegister().deregisterOutput: " + deregisterOutput);

		} catch (Exception e) {
			logger.error("Publisher DeRegister is not done: " + e.getMessage());
		}finally
		{
			closeSession(session);
		}

	}

	@Override
	public void subscriberDeRegister(String topic, String fqdn, String uid, String ipAddress) {

		String deregisterOutput = null;
		Session session =null;
		try {

			 session = getSession();
			ProcedureCall call = session.createStoredProcedureCall("deregisterSubscriber");

			call.registerParameter(1, String.class, ParameterMode.IN).bindValue(uid);
			call.registerParameter(2, String.class, ParameterMode.IN).bindValue(fqdn);
			call.registerParameter(3, String.class, ParameterMode.IN).bindValue(topic);
			call.registerParameter(4, String.class, ParameterMode.IN).bindValue(ipAddress);
			call.registerParameter(5, String.class, ParameterMode.INOUT);

			deregisterOutput = (String) call.getOutputs().getOutputParameterValue(5);
			logger.metrics("subscriberRegister().deregisterOutput: " + deregisterOutput);
		} catch (Exception e) {
			logger.error("Subscriber DeRegister is not done: " + e.getMessage());
		}
		finally
		{
			closeSession(session);
		}

	}

	public void closeSession(Session session) {
		if (session != null) {
			try {
				session.flush();
				session.clear();
				session.close();
				logger.debug("Session closed " + session.isOpen());
			} catch (HibernateException ignored) {
				logger.error("Couldn't close Session" + ignored);
			}
		}

	}

}
