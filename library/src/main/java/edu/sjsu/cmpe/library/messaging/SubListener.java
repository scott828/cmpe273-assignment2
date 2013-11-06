package edu.sjsu.cmpe.library.messaging;

import java.net.MalformedURLException;
import java.net.URL;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.Book.Status;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;

public class SubListener implements Runnable {

	/** bookRepository instance */
	private final BookRepositoryInterface bookRepository;

	LibraryServiceConfiguration configuration;

	private String topicName;

	private String user;
	private String password;
	private String host;
	private int port;

	private StompJmsConnectionFactory factory;
	private Connection connection;
	private Session session;

	private final Logger log = LoggerFactory.getLogger(getClass());

	public SubListener(LibraryServiceConfiguration configuration,
			BookRepositoryInterface bookRepository) {
		this.configuration = configuration;
		this.bookRepository = bookRepository;

		topicName = configuration.getStompTopicName();
		user = configuration.getApolloUser();
		password = configuration.getApolloPassword();
		host = configuration.getApolloHost();
		port = configuration.getApolloPort();
	}

	@Override
	public void run() {

		System.out.println("Listener is working...");

		factory = new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://" + host + ":" + port);

		while (true) {
			try {
				connection = factory.createConnection(user, password);

				connection.start();
				session = connection.createSession(false,
						Session.AUTO_ACKNOWLEDGE);

				connection = factory.createConnection(user, password);

				connection.start();
				Session session = connection.createSession(false,
						Session.AUTO_ACKNOWLEDGE);
				Destination dest = new StompJmsDestination(topicName);

				MessageConsumer consumer = session.createConsumer(dest);
				long start = System.currentTimeMillis();
				long count = 1;

				while (true) {
					System.out.println("Waiting for messages...");
					Message msg = consumer.receive();
					if (msg instanceof TextMessage) {
						String body = ((TextMessage) msg).getText();
						if ("SHUTDOWN".equals(body)) {
							long diff = System.currentTimeMillis() - start;
							System.out.println(String.format(
									"Received %d in %.2f seconds", count,
									(1.0 * diff / 1000.0)));
							break;
						}
						log.info("Received message = " + body);

						String delims = "[:]";
						String[] tokens = body.split(delims);
						int size = tokens.length;

						for (int i = 0; i < size; i++)
							System.out.println(tokens[i]);

						if (size != 5) {
							log.warn("Unexpected message size: " + size);
						}

						long isbn = Long.valueOf(tokens[0]);
						Book book = bookRepository.getBookByISBN(isbn);

						if (book != null) {
							book.setStatus(Status.available);
							log.info("Find book: " + isbn);
						} else {
							book = new Book();
							book.setIsbn(isbn);
							book.setTitle(tokens[1]);
							book.setCategory(tokens[2]);
							StringBuilder url = new StringBuilder();

							url.append(tokens[3]);
							url.append(":");
							url.append(tokens[4]);
							log.info("URL is: " + url);
							try {
								book.setCoverimage(new URL(url.toString()));
							} catch (MalformedURLException e) {
								log.warn("Unexpected message content: "
										+ msg.getClass());
							}

							log.info("Save book: " + book);

							bookRepository.addBook(book);
						}

					} else {
						log.warn("Unexpected message type: " + msg.getClass());
					}
				}

			} catch (JMSException e) {
				log.error("Unexpected exception: " + e);
			} finally {
				System.out.println("Close connection...");

				try {
					connection.close();
				} catch (JMSException e) {
					log.error("Failed to close connection: exception: " + e);
				}

			}
		}

	}

}
