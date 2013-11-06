package edu.sjsu.cmpe.library.messaging;

import edu.sjsu.cmpe.library.domain.Book;

public interface MessagingServiceInterface {
	
    /**
     * Retrieve an existing book by ISBN
     * 
     * @param isbn
     *            a valid ISBN
     * @return a book instance
     */
    void sendMessageToQueue(String msg);


}
