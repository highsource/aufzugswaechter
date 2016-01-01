package org.hisrc.azw.twitter.tests;

import java.text.MessageFormat;
import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

public class HelloWorld {

	private static final String BUCKET = "aufzugswaechter";
	private static final String PROPERTIES_KEY = "twitter.properties";

	public static void main(String[] args) throws Exception {

		final AmazonS3Client client = Region.getRegion(Regions.EU_CENTRAL_1)
				.createClient(AmazonS3Client.class, null, null);

		final Properties properties = new Properties();
		properties.load(client.getObject(BUCKET, PROPERTIES_KEY)
				.getObjectContent());

		final CamelContext context = new DefaultCamelContext();

		final ProducerTemplate template = context.createProducerTemplate();
		context.start();

		template.sendBody(
				MessageFormat
						.format("twitter:timeline/user?accessToken={0}&accessTokenSecret={1}&consumerKey={2}&consumerSecret={3}",
								properties.getProperty("accessToken"),
								properties.getProperty("accessTokenSecret"),
								properties.getProperty("consumerKey"),
								properties.getProperty("consumerSecret")),
				"Test tweet with Twitter credentials loaded from AWS S3 Bucket");
	}
}
