package org.hisrc.camel.component.aws.sns;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.aws.sns.SnsConfiguration;
import org.apache.camel.component.aws.sns.SnsConstants;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SetTopicAttributesRequest;

/**
 * A Producer which sends messages to the Amazon Web Service Simple Notification
 * Service <a href="http://aws.amazon.com/sns/">AWS SNS</a>
 */
public class DynamicSnsProducer extends DefaultProducer {

	private static final Logger LOG = LoggerFactory.getLogger(DynamicSnsProducer.class);

	public DynamicSnsProducer(Endpoint endpoint) {
		super(endpoint);
	}

	public void process(Exchange exchange) throws Exception {

		// TODO cache arns and don't create if not necessary
		final String topic = determineTopic(exchange);

		// creates a new topic, or returns the URL of an existing one
		CreateTopicRequest request = new CreateTopicRequest(topic);

		LOG.trace("Creating topic [{}] with request [{}]...", topic, request);

		final AmazonSNS snsClient = getEndpoint().getSNSClient();
		
		CreateTopicResult result = snsClient.createTopic(request);

		final String topicArn = result.getTopicArn();
		LOG.trace("Topic created with Amazon resource name: {}", topicArn);

		final SnsConfiguration configuration = getEndpoint().getConfiguration();
		
		if (ObjectHelper.isNotEmpty(configuration.getPolicy())) {
			LOG.trace("Updating topic [{}] with policy [{}]", topicArn, configuration.getPolicy());

			snsClient.setTopicAttributes(
					new SetTopicAttributesRequest(topicArn, "Policy", configuration.getPolicy()));

			LOG.trace("Topic policy updated");
		}

		PublishRequest publishRequest = new PublishRequest();

		publishRequest.setTopicArn(topicArn);
		publishRequest.setSubject(determineSubject(exchange));
		publishRequest.setMessageStructure(determineMessageStructure(exchange));
		publishRequest.setMessage(exchange.getIn().getBody(String.class));

		LOG.trace("Sending request [{}] from exchange [{}]...", publishRequest, exchange);

		PublishResult publishResult = snsClient.publish(publishRequest);

		LOG.trace("Received result [{}]", publishResult);

		Message message = getMessageForResponse(exchange);
		message.setHeader(SnsConstants.MESSAGE_ID, publishResult.getMessageId());
	}

	private Message getMessageForResponse(Exchange exchange) {
		if (exchange.getPattern().isOutCapable()) {
			Message out = exchange.getOut();
			out.copyFrom(exchange.getIn());
			return out;
		}

		return exchange.getIn();
	}

	private String determineTopic(Exchange exchange) {
		String subject = exchange.getIn().getHeader(DynamicSnsConstants.TOPIC, String.class);
		if (subject == null) {
			subject = getConfiguration().getTopicName();
		}

		return subject;
	}

	private String determineSubject(Exchange exchange) {
		String subject = exchange.getIn().getHeader(SnsConstants.SUBJECT, String.class);
		if (subject == null) {
			subject = getConfiguration().getSubject();
		}

		return subject;
	}

	private String determineMessageStructure(Exchange exchange) {
		String structure = exchange.getIn().getHeader(SnsConstants.MESSAGE_STRUCTURE, String.class);
		if (structure == null) {
			structure = getConfiguration().getMessageStructure();
		}

		return structure;
	}

	protected SnsConfiguration getConfiguration() {
		return getEndpoint().getConfiguration();
	}

	@Override
	public String toString() {
		return "DynamicSnsProducer[" + URISupport.sanitizeUri(getEndpoint().getEndpointUri()) + "]";
	}

	@Override
	public DynamicSnsEndpoint getEndpoint() {
		return (DynamicSnsEndpoint) super.getEndpoint();
	}
}