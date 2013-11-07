package edu.sjsu.cmpe.library.messaging;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;

public class MessagingService implements MessagingServiceInterface {

	private static MessagingService instance = null;

	LibraryServiceConfiguration configuration = null;

	private String queueName;
	private String topicName;

	private String user;
	private String password;
	private String host;
	private int port;

	private StompJmsConnectionFactory factory;
	private Connection connection;
	private Session session;

	private final Logger log = LoggerFactory.getLogger(getClass());

	protected MessagingService() {
		// Exists only to defeat instantiation.
	}

	public static MessagingService getInstance() {
		if (instance == null) {
			instance = new MessagingService();
		}
		return instance;
	}

	public void setConfiguration(LibraryServiceConfiguration configuration) {
		this.configuration = configuration;
		queueName = configuration.getStompQueueName();
		topicName = configuration.getStompTopicName();
		user = configuration.getApolloUser();
		password = configuration.getApolloPassword();
		host = configuration.getApolloHost();
		port = configuration.getApolloPort();
	}

	/*
	public void start() {
		factory = new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://" + host + ":" + port);

		try {
			connection = factory.createConnection(user, password);

			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/


	@Override
	public void sendMessageToQueue(String message) {
		
		factory = new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://" + host + ":" + port);

		try {
			connection = factory.createConnection(user, password);

			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Destination dest = new StompJmsDestination(queueName);
		MessageProducer producer;
		try {
			producer = session.createProducer(dest);

			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			System.out.println("Sending messages to " + queueName + "...");
		
			TextMessage msg = session.createTextMessage(message);
			msg.setLongProperty("id", System.currentTimeMillis());
			producer.send(msg);

			//producer.send(session.createTextMessage("SHUTDOWN"));
			connection.close();
		} catch (JMSException e) {
			System.out.println("Exception catched in Sending messages: " + e + "...");
		}

	}

}
