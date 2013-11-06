package edu.sjsu.cmpe.procurement.service;

import java.util.Iterator;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.dto.ShippedBook;
import edu.sjsu.cmpe.procurement.dto.ShippedBooks;
import edu.sjsu.cmpe.procurement.repository.ProcurementRepository;

public class MessagingService implements MessagingServiceInterface {

	private static MessagingService instance = null;

	ProcurementServiceConfiguration configuration = null;

	private String queueName;
	private String topicPrefix;
	private String topicName;

	private String user;
	private String password;
	private String host;
	private int port;

	private StompJmsConnectionFactory factory;
	private Connection connection;
	private Session session;
	private MessageProducer producer;

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

	public void setConfiguration(ProcurementServiceConfiguration configuration) {
		this.configuration = configuration;
		queueName = configuration.getStompQueueName();
		topicPrefix = configuration.getStompTopicPrefix();
		user = configuration.getApolloUser();
		password = configuration.getApolloPassword();
		host = configuration.getApolloHost();
		port = configuration.getApolloPort();
	}

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

	public void stop() {

		try {
			// Pause for 1 seconds
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connection.close();
			session = null;
			connection = null;
			factory = null;
			System.out.println("Session is closed");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void publishMessageToTopicComputer(String data) {

		String computerTopicName = topicPrefix + "computer";

		System.out.println("the topc: " + computerTopicName);

		Destination dest = new StompJmsDestination(computerTopicName);

		try {
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			System.out.println("Session is " + session);

			MessageProducer producer = session.createProducer(dest);

			System.out.println("producer is " + producer);

			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			TextMessage msg = null;

			msg = session.createTextMessage(data);
			msg.setLongProperty("id", System.currentTimeMillis());
			producer.send(msg);

			System.out.println("Sent out message: " + msg + " to the topc: "
					+ computerTopicName);

			if (data == null) {
				System.out
						.println("There is no shipped books. So no need to send anyting to the topic");
			}

			// producer.send(session.createTextMessage("SHUTDOWN"));
			// connection.close();
		} catch (JMSException e) {
			System.out
					.println("There is exception in sending message to the topic: "
							+ computerTopicName + " with exception:" + e);

		} catch (Exception e) {
			System.out
					.println("There is exception in sending message to the topic: "
							+ computerTopicName + " with exception:" + e);

		}
	}

	public void publishMessageToTopic() {

		String topicName = topicPrefix + "all";
		System.out.println("the topc: " + topicName);
		Destination allTopicDesc = new StompJmsDestination(topicName);

		try {
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			System.out.println("Session is " + session);

			MessageProducer producer = session.createProducer(allTopicDesc);

			System.out.println("producer is " + producer);

			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			StringBuilder data = null;
			TextMessage msg = null;
			ShippedBooks shippedBooks = ProcurementRepository.getInstance()
					.getShippedBooks();

			ShippedBook book = null;
			Iterator<ShippedBook> iterator = shippedBooks.getShippedBooks()
					.iterator();
			while (iterator.hasNext()) {
				data = new StringBuilder();
				book = iterator.next();

				data.append(book.getIsbn());
				data.append(":");
				// data.append("\"");
				data.append(book.getTitle());
				// data.append("\"");
				data.append(":");
				// data.append("\"");
				data.append(book.getCategory());
				// data.append("\"");
				data.append(":");
				// data.append("\"");
				data.append(book.getCoverimage());
				// data.append("\"");

				if (book.getCategory().equalsIgnoreCase("computer")) {
					String newData = data.toString();
					publishMessageToTopicComputer(newData);
					// Pause for 1 seconds
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.out.println("Failed in sleeping: " + e);
					}
				}

				msg = session.createTextMessage(data.toString());
				msg.setLongProperty("id", System.currentTimeMillis());
				producer.send(msg);

				System.out.println("Sent out message: " + msg
						+ " to the topc: " + topicName);
			}

			if (data == null) {
				System.out
						.println("There is no shipped books. So no need to send anyting to the topic");
			}

			// producer.send(session.createTextMessage("SHUTDOWN"));
			// connection.close();
		} catch (JMSException e) {
			System.out
					.println("There is exception in sending message to the topic: "
							+ topicName + " with exception:" + e);

		} catch (Exception e) {
			System.out
					.println("There is exception in sending message to the topic: "
							+ topicName + " with exception:" + e);

		}
	}

	public void publishMessageToAllTopic(String data) {

		String topicName = topicPrefix + "all";
		System.out.println("the topc: " + topicName);
		Destination allTopicDesc = new StompJmsDestination(topicName);

		try {
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			System.out.println("Session is " + session);

			MessageProducer producer = session.createProducer(allTopicDesc);

			System.out.println("producer is " + producer);

			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			TextMessage msg = null;

			msg = session.createTextMessage(data);
			msg.setLongProperty("id", System.currentTimeMillis());
			producer.send(msg);

			System.out.println("Sent out message: " + msg + " to the topc: "
					+ topicName);

	

			// producer.send(session.createTextMessage("SHUTDOWN"));
			// connection.close();
		} catch (JMSException e) {
			System.out
					.println("There is exception in sending message to the topic: "
							+ topicName + " with exception:" + e);

		} catch (Exception e) {
			System.out
					.println("There is exception in sending message to the topic: "
							+ topicName + " with exception:" + e);

		}
	}

	public void getMessageFromQueue() {

		Destination dest = new StompJmsDestination(queueName);
		MessageConsumer consumer;
		try {
			consumer = session.createConsumer(dest);

			System.out
					.println("Waiting for messages from " + queueName + "...");

			long waitUntil = 5000; // wait for 5 sec
			while (true) {
				Message msg = consumer.receive(waitUntil);
				if (msg instanceof TextMessage) {
					String body = ((TextMessage) msg).getText();
					System.out.println("Received message = " + body);

					String delims = "[:]";
					String[] tokens = body.split(delims);

					ProcurementRepository.getInstance().getOrder()
							.addBook(Integer.valueOf(tokens[1]));
					
				} else if (msg == null) {
					System.out
							.println("No new messages. Existing due to timeout - "
									+ waitUntil / 1000 + " sec");
					break;
				} else {
					System.out.println("Unexpected message type: "
							+ msg.getClass());
				}
			} // end while loop
				// connection.close();
			System.out.println("Done");

	
		} catch (JMSException e) {
			System.out
					.println("Unexpected exception caught when getting message from queue: "
							+ e);
		}

	}
}
