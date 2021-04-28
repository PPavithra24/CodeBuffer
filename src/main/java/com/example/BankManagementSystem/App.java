package com.example.BankManagementSystem;

import java.util.ArrayList;
import java.util.List;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.example.BankManagementSystem.Model.Customer;
import com.example.BankManagementSystem.Model.LoanDetails;
import com.example.BankManagementSystem.Model.Login;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class App {
	public static void main(String[] args) {

		List<Customer> employees = new ArrayList<>();
		Customer customer = new Customer();
		Vertx vertx = Vertx.vertx();
		HttpServer httpServer = vertx.createHttpServer();
		Router router = Router.router(vertx);
		

		Route handler1 = router.post("/login").handler(BodyHandler.create()).handler(routingContext -> {
			final Login login = Json.decodeValue(routingContext.getBody(), Login.class);

			CouchbaseEnvironment envr = DefaultCouchbaseEnvironment.builder().connectTimeout(1000).build();

			Cluster cluster = CouchbaseCluster.create(envr, "127.0.0.1");
			cluster.authenticate("root", "123456");
			Bucket bucket = cluster.openBucket("example");

			String uniquekey = login.getUsername() + login.getPassword();
			final JsonDocument answer = bucket.get(uniquekey);
			JsonObject object = null;
			if (answer != null) {

				object = answer.content();
				customer.setName(object.getString("name"));
				customer.setUsername(object.getString("username"));
				customer.setPassword(object.getString("password"));
			}
			if (object != null) {
				HttpServerResponse response = routingContext.response();
				response.setChunked(true);
				response.end("login successfully");
				System.out.println("login credentials are present" + object.getString("name")
						+ object.getString("username") + object.getString("password"));
			} else {
				HttpServerResponse response = routingContext.response();
				response.setChunked(true);
				response.end("Invalid Credentials");
			}

		});


		Route handler2 = router.post("/register").handler(BodyHandler.create()).handler(routingContext -> {
			final Customer employee = Json.decodeValue(routingContext.getBody(), Customer.class);

			CouchbaseEnvironment envr = DefaultCouchbaseEnvironment.builder().connectTimeout(1000).build();

			Cluster cluster = CouchbaseCluster.create(envr, "127.0.0.1");
			cluster.authenticate("root", "123456");
			Bucket bucket = cluster.openBucket("example");

			JsonObject json = JsonObject.empty().put("username", employee.getUsername()).put("name", employee.getName())
					.put("password", employee.getPassword());

			String uniquekey = employee.getUsername() + employee.getPassword();

			JsonDocument content = JsonDocument.create(uniquekey, json);
			bucket.insert(content);

			HttpServerResponse response = routingContext.response();
			response.setChunked(true);
			employees.add(employee);
			response.end(employees.size() + " Data Added Successfully");

		});
		Route handler3 = router.post("/update").handler(BodyHandler.create()).handler(routingContext -> {
			final Customer customer1;

			if (customer.getName() == "") {
				HttpServerResponse response = routingContext.response();
				response.setChunked(true);
				response.end("User Not logged In Successfully ");
			} else {

				customer1 = Json.decodeValue(routingContext.getBody(), Customer.class);

				String uniquekey = customer.getUsername() + customer.getPassword();
				customer.setName(customer1.getName());
				customer.setUsername(customer1.getUsername());
				customer.setPassword(customer1.getPassword());

				HttpServerResponse response = routingContext.response();
				response.setChunked(true);
				response.end("Employee updated successfully");

				CouchbaseEnvironment envr = DefaultCouchbaseEnvironment.builder().connectTimeout(1000).build();

				Cluster cluster = CouchbaseCluster.create(envr, "127.0.0.1");
				cluster.authenticate("root", "123456");
				Bucket bucket = cluster.openBucket("example");

				JsonObject json = JsonObject.empty().put("username", customer.getUsername())
						.put("name", customer.getName()).put("password", customer.getPassword());

				JsonDocument content = JsonDocument.create(customer.getUsername() + customer.getPassword(), json);
				bucket.remove(uniquekey);

				bucket.upsert(content);

			}
		});

		Route handler4 = router.get("/logout").handler(routingContext -> {

			if (customer.getName() == "") {

				HttpServerResponse response = routingContext.response();
				response.setChunked(true);
				response.end("User Not logged In Successfully ");

			} else {

				customer.setName("");
				HttpServerResponse response = routingContext.response();
				response.setChunked(true);
				response.end("Logout successfully ");

			}
		});

		Route handler5 = router.post("/applyforloan").handler(BodyHandler.create()).handler(routingContext -> {
			if(customer.getUsername() != "") {
				final LoanDetails loan = Json.decodeValue(routingContext.getBody(), LoanDetails.class);

				CouchbaseEnvironment envr = DefaultCouchbaseEnvironment.builder().connectTimeout(1000).build();

				Cluster cluster = CouchbaseCluster.create(envr, "127.0.0.1");
				cluster.authenticate("root", "123456");
				Bucket bucket = cluster.openBucket("example");
				
				JsonObject json = JsonObject.empty().put("LoanAmount","125676").put("LoanType", loan.getLoanType())
						.put("RateOfInterest", "12.5")
						.put("date", loan.getDate())
						.put("DurationOfLoan", loan.getDurationOfLoan());

				String uniquekey = customer.getAccountNumber();

				JsonDocument content = JsonDocument.create(uniquekey, json);
				bucket.insert(content);
				
				HttpServerResponse response = routingContext.response();
				response.setChunked(true);
				response.end("Loan Applied Successfully");
				
			}else {
				HttpServerResponse response = routingContext.response();
				response.setChunked(true);
				response.end("User Not logged In Successfully");
				
			}
			

		});

		
		//httpServer.requestHandler(router).listen(8080);
		httpServer.requestHandler(router::accept).listen(8080);
		
		vertx.close();
	}

}



