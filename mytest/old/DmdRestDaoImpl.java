

@Repository
public class DmdRestDaoImpl implements DmdRestDao {
	static private final DmdLogger logger = DmdLogger.getLogger(
			DmdRestDaoImpl.class, DmdLogger.DMD_API);
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public String publisherRegister(String topic, String fqdn, String ipAddress) {

		String registerOutput = null;
		try {
			logger.metrics("publisherRegister topic: "+topic+", fqdn: "+fqdn);
			StoredProcedureQuery query = entityManager.createStoredProcedureQuery("registerPublisher")
					.registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(4, String.class, ParameterMode.OUT).setParameter(1, fqdn)
					.setParameter(2, topic).setParameter(3, ipAddress);
			query.execute();

			registerOutput = (String) query.getOutputParameterValue(4);
			logger.metrics("publisherRegister().registerOutput: " + registerOutput);
		} catch (Exception e) {
			logger.error("Publisher Register is not done: "+e.getMessage());
		}
		return registerOutput;
	}

	@Override
	public String subscriberRegister(Topic topic) {
		String registerOutput = null;
		try {
			logger.metrics("subscriberRegister topic: "+topic.toString());
			StoredProcedureQuery query = entityManager.createStoredProcedureQuery("registerSubscriber")
					.registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(4, Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(7, String.class, ParameterMode.OUT)
					.setParameter(1, topic.getSubscriberUID()).setParameter(2, topic.getFqdn())
					.setParameter(3, topic.getTopicName())
					.setParameter(4, Integer.parseInt(topic.getDeliveryGuarantee()))
					.setParameter(5, Integer.parseInt(topic.getDurableSubscriptionFlag()))
					.setParameter(6, topic.getIpAddress());

			query.execute();
			registerOutput = (String) query.getOutputParameterValue(7);
			logger.metrics("subscriberRegister().registerOutput: " + registerOutput);
		} catch (Exception e) {
			logger.error("Subscriber Register is not done: "+e.getMessage());
		}
		return registerOutput;
	}

	@Override
	public void publisherDeRegister(String topic, String fqdn, String ipAddress) {

		String deregisterOutput = null;
		try {
			logger.metrics("publisherDeRegister topic: "+topic+","+fqdn);
			StoredProcedureQuery query = entityManager.createStoredProcedureQuery("deregisterPublisher")
					.registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(4, String.class, ParameterMode.OUT).setParameter(1, fqdn)
					.setParameter(2, topic).setParameter(3, ipAddress);
			query.execute();
			deregisterOutput = (String) query.getOutputParameterValue(4);
			logger.metrics("publisherDeRegister().deregisterOutput: " + deregisterOutput);

		} catch (Exception e) {
			logger.error("Publisher DeRegister is not done: "+e.getMessage());
		}

	}

	@Override
	public void subscriberDeRegister(String topic, String fqdn, String uid, String ipAddress) {

		String deregisterOutput = null;
		try {
			logger.metrics("subscriberDeRegister topic: "+topic+","+fqdn+","+uid);
			StoredProcedureQuery query = entityManager.createStoredProcedureQuery("deregisterSubscriber")
					.registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
					.registerStoredProcedureParameter(5, String.class, ParameterMode.OUT)
					.setParameter(1, uid)
					.setParameter(2, fqdn)
					.setParameter(3, topic)
					.setParameter(4, ipAddress);

			query.execute();
			deregisterOutput = (String) query.getOutputParameterValue(5);
			logger.metrics("subscriberRegister().deregisterOutput: " + deregisterOutput);
		} catch (Exception e) {
			logger.error("Subscriber DeRegister is not done: "+e.getMessage());
		}

	}

}
