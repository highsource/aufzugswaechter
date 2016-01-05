package org.hisrc.azw.amazonaws.services.sns;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeResult;

public class HelloWorldSNS {

	public static void main(String[] args) throws Exception {

		final AmazonSNSClient client = Region.getRegion(Regions.EU_CENTRAL_1).createClient(AmazonSNSClient.class, null,
				null);
		CreateTopicResult createTopic = client.createTopic("facilities");
		createTopic.getTopicArn();
		SubscribeResult subscribe = client.subscribe(createTopic.getTopicArn(), "email", "valikov@gmx.net");
		final PublishRequest publishRequest = new PublishRequest(createTopic.getTopicArn(), "Test message");
		client.publish(publishRequest);
	}
}
