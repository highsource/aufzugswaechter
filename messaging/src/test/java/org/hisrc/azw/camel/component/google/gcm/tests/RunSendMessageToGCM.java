package org.hisrc.azw.camel.component.google.gcm.tests;

import java.util.Properties;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class RunSendMessageToGCM {

	// private static final String BUCKET = "aufzugswaechter";
	// private static final String PROPERTIES_KEY = "gcm.properties";

	public static void main(String[] args) throws Exception {

		// final AmazonS3Client client =
		// Region.getRegion(Regions.EU_CENTRAL_1).createClient(AmazonS3Client.class,
		// null,
		// null);
		//
		final Properties properties = new Properties();
		properties.put("gcm.apiKey", "AIzaSyD_x_oz3X_2h4Vf1xfrqoGjzaOvLqcMiUo");
		// properties.load(client.getObject(BUCKET,
		// PROPERTIES_KEY).getObjectContent());

//		final CamelContext context = new DefaultCamelContext();
//
//		final RouteBuilder routeBuilder = new RouteBuilder() {
//
//			@Override
//			public void configure() throws Exception {
//				from("direct:laconic").to(MessageFormat.format("google-cloud-messaging:facilities?apiKey={0}",
//						properties.getProperty("gcm.apiKey")));
//
//			}
//		};
//		context.addRoutes(routeBuilder);
//
//		final ProducerTemplate template = context.createProducerTemplate();
//		context.start();
//		Route route = context.getRoutes().get(0);
//		final Map<String, Object> headers = new HashMap<>();
//		headers.put("to", "/topics/facility-10213788"));

//		final Map<String, String> body = new HashMap<>();
//		body.put("facilityEquipmentnumber", "10213788");
//		body.put("stationName", "Arnstadt Hbf");
//		body.put("facilityDescription", "Aufzug zu Bstg 2/3");
//		body.put("facilityState", "INACTIVE");

		final Sender sender = new Sender(properties.getProperty("gcm.apiKey"));
		Message message = new Message.Builder()
		    .addData("facilityEquipmentnumber", "10213788e")
		    .addData("stationName", "Arnstadt Hbf")
		    .addData("facilityDescription", "Aufzug zu Bstg 2/3")
		    .addData("facilityState", "INACTIVE")
		    .build();
		Result result = sender.send(message, "/topics/facility-10213788", 0);
		result.toString();
	}
}
