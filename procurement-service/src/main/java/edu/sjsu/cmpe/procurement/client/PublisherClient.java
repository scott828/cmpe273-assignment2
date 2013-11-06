package edu.sjsu.cmpe.procurement.client;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import edu.sjsu.cmpe.procurement.domain.BookOrder;
import edu.sjsu.cmpe.procurement.dto.ShippedBooks;
import edu.sjsu.cmpe.procurement.repository.ProcurementRepository;
import edu.sjsu.cmpe.procurement.util.JsonUtil;

public class PublisherClient {

	private Client client = null;

	public PublisherClient(Client client) {
		this.client = client;
	}

	public PublisherClient() {
		client = Client.create();
	}

	public void orderBooks(BookOrder myOrder) {

		WebResource webResource = client
				.resource("http://54.215.210.214:9000/orders");

		System.out.println("The book order is:" + myOrder.toString());

		ClientResponse response = webResource.type("application/json").post(
				ClientResponse.class, myOrder.toString());

		System.out.println("Output from Server .... \n");
		String output = response.getEntity(String.class);
		System.out.println(output);

		System.out.println("Empty the old order queue \n");
		ProcurementRepository.getInstance().setOrder(new BookOrder());

	}

	public void getOrder() {

		WebResource webResource = client
				.resource("http://54.215.210.214:9000/orders/05125");

		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

		String output = response.getEntity(String.class);

		System.out.println("Output from Server .... \n" + output);

		ShippedBooks shippedBooks = JsonUtil.decode(output, ShippedBooks.class);

		System.out.println("Shipped Books Object is:" + shippedBooks.toString());
		
		ProcurementRepository.getInstance().setShippedBooks(shippedBooks);

	}
}
