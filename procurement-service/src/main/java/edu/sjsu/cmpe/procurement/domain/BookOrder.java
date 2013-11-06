package edu.sjsu.cmpe.procurement.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BookOrder {

	@JsonProperty
	@NotEmpty
	private String id = "05125";

	@JsonProperty("order_book_isbns")
	@NotEmpty
	private List<Integer> order_book_isbns = new ArrayList<Integer>();

	public void addBook(Integer bookIsbn) {
		order_book_isbns.add(bookIsbn);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Integer> getOrder_book_isbns() {
		return order_book_isbns;
	}

	public void setOrder_book_isbns(List<Integer> order_book_isbns) {
		this.order_book_isbns = order_book_isbns;
	}
	

	public String toString() {
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e1) {
			System.out.println("Failed to convert object to JSON String" + e1);
			return null;
		}
		
          	
	}

	
}
