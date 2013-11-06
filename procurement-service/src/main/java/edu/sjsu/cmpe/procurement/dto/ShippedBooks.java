package edu.sjsu.cmpe.procurement.dto;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShippedBooks {

	@JsonProperty("shipped_books")
	@NotEmpty
	private List<ShippedBook> shippedBooks = new ArrayList<ShippedBook>();
		

	public List<ShippedBook> getShippedBooks() {
		return shippedBooks;
	}


	public void setShippedBooks(List<ShippedBook> shippedBooks) {
		this.shippedBooks = shippedBooks;
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
