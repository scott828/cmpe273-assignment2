package edu.sjsu.cmpe.procurement.repository;

import edu.sjsu.cmpe.procurement.domain.BookOrder;
import edu.sjsu.cmpe.procurement.dto.ShippedBooks;


public class ProcurementRepository {
	
	private static ProcurementRepository instance = null;
	
	private BookOrder order = new BookOrder();
	
	private ShippedBooks shippedBooks = null;
	
	public ShippedBooks getShippedBooks() {
		return shippedBooks;
	}

	public void setShippedBooks(ShippedBooks shippedBooks) {
		this.shippedBooks = shippedBooks;
	}

	public BookOrder getOrder() {
		return order;
	}



	public void setOrder(BookOrder order) {
		this.order = order;
	}

	protected ProcurementRepository() {
		// Exists only to defeat instantiation.
	}

	public static ProcurementRepository getInstance() {
		if (instance == null) {
			instance = new ProcurementRepository();
		}
		return instance;
	}
	
	

}
