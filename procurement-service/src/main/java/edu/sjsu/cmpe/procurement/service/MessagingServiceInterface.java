package edu.sjsu.cmpe.procurement.service;

import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;



public interface MessagingServiceInterface {
	
    /**
     * Retrieve an existing book by ISBN
     * 
     * @param isbn
     *            a valid ISBN
     * @return a book instance
     */
   public void getMessageFromQueue();

   
   /**
    * Retrieve an existing book by ISBN
    * 
    * @param isbn
    *            a valid ISBN
    * @return a book instance
    */
	public void setConfiguration(ProcurementServiceConfiguration cfg);


}
