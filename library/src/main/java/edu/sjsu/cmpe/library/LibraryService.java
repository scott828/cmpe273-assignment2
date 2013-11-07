package edu.sjsu.cmpe.library;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import edu.sjsu.cmpe.library.api.resources.BookResource;
import edu.sjsu.cmpe.library.api.resources.RootResource;
import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.messaging.MessagingService;
import edu.sjsu.cmpe.library.messaging.SubListener;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.ui.resources.HomeResource;

public class LibraryService extends Service<LibraryServiceConfiguration> {

	private final Logger log = LoggerFactory.getLogger(getClass());  

	public static void main(String[] args) throws Exception {
		new LibraryService().run(args);
	}

	@Override
	public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap) {
		bootstrap.setName("library-service");
		bootstrap.addBundle(new ViewBundle());
		bootstrap.addBundle(new AssetsBundle());
	}

	@Override
	public void run(LibraryServiceConfiguration configuration,
			Environment environment) throws Exception {
		// This is how you pull the configurations from library_x_config.yml
		String queueName = configuration.getStompQueueName();
		String topicName = configuration.getStompTopicName();
		
		String libaryName = configuration.getLibraryName();
		log.debug("Queue name is {}. Topic name is {}. Libary name is {}", queueName, topicName, libaryName);
		
		// Set up Messaging service to include Apollo STOMP Broker URL and login

		MessagingService messageService = MessagingService.getInstance();
		messageService.setConfiguration(configuration);
		//messageService.start();

		/** Root API */
		environment.addResource(RootResource.class);
		/** Books APIs */
		BookRepositoryInterface bookRepository = BookRepository.getInstance();
		environment.addResource(new BookResource(bookRepository, libaryName));

		/** UI Resources */
		environment.addResource(new HomeResource(bookRepository));
		
    	int numThreads = 1;
	    ExecutorService executor = Executors.newFixedThreadPool(numThreads);	 
 
    	System.out.println("About to submit the background task");
    	executor.execute(new SubListener(configuration, bookRepository));
    	System.out.println("Submitted the background task");
    
    	//executor.shutdown();
    	//System.out.println("Finished the background task");
	}
}
