package edu.sjsu.cmpe.procurement.jobs;

import java.util.Iterator;

import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.ProcurementService;
import edu.sjsu.cmpe.procurement.client.PublisherClient;
import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.domain.BookOrder;
import edu.sjsu.cmpe.procurement.dto.ShippedBook;
import edu.sjsu.cmpe.procurement.dto.ShippedBooks;
import edu.sjsu.cmpe.procurement.repository.ProcurementRepository;
import edu.sjsu.cmpe.procurement.service.MessagingService;
import edu.sjsu.cmpe.procurement.service.MessagingServiceInterface;

/**
 * This job will run at every 5 second.
 */
@Every("10s")
public class ProcurementSchedulerJob extends Job {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private ProcurementServiceConfiguration cfg = null;

	public ProcurementServiceConfiguration getCfg() {
		return cfg;
	}

	public void setCfg(ProcurementServiceConfiguration cfg) {
		this.cfg = cfg;
	}

	@Override
	public void doJob() {
		/*
		 * String strResponse = ProcurementService.jerseyClient.resource(
		 * "http://ip.jsontest.com/").get(String.class);
		 * log.debug("Response from jsontest.com: {}", strResponse);
		 */

		// Set up Messaging service to include Apollo STOMP Broker URL and login

		MessagingService messageService = MessagingService.getInstance();
		messageService.setConfiguration(ProcurementService.configuration);
		messageService.start();
		messageService.getMessageFromQueue();

		BookOrder myOrder = ProcurementRepository.getInstance().getOrder();

		if (myOrder.getOrder_book_isbns().isEmpty()) {
			log.info("There is no book order. Do nothing...");
		} else {

			PublisherClient clientService = new PublisherClient();

			clientService.orderBooks(myOrder);
			clientService.getOrder();
			
			StringBuilder data = null;
			
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
					messageService.publishMessageToTopicComputer(newData);
					// Pause for 1 seconds
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.out.println("Failed in sleeping: " + e);
					}
				}

				messageService.publishMessageToAllTopic(data.toString());
			}

			
		}
		messageService.stop();

	}
}
