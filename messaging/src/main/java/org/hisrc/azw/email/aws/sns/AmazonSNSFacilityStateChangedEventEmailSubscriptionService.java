package org.hisrc.azw.email.aws.sns;

import java.text.MessageFormat;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.email.FacilityStateChangedEventEmailSubscriptionService;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.service.FacilityService;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

public class AmazonSNSFacilityStateChangedEventEmailSubscriptionService
		implements FacilityStateChangedEventEmailSubscriptionService {

	private static final String EMAIL_PROTOCOL = "email";
	private static final String FACILITY_TOPIC_NAME_PATTERN = "facility-{0,number,#}";
	private static final String FACILITIES_TOPIC_NAME = "facilities";
	private AmazonSNSClient client;

	public AmazonSNSClient getClient() {
		return client;
	}

	public void setClient(AmazonSNSClient client) {
		this.client = client;
	}

	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public AmazonSNSFacilityStateChangedEventEmailSubscriptionService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void subscribeForAllFacilities(String email) {
		CreateTopicResult topicResult = getClient().createTopic(FACILITIES_TOPIC_NAME);
		getClient().subscribe(new SubscribeRequest(topicResult.getTopicArn(), EMAIL_PROTOCOL, email));
	}

	@Override
	public void subscribeForFacility(Long equipmentnumber, String email) {
		Validate.notNull(equipmentnumber);
		final Facility facility = getFacilityService().findByEquipmentnumber(equipmentnumber);
		if (facility != null) {
			String topicName = MessageFormat.format(FACILITY_TOPIC_NAME_PATTERN, equipmentnumber);
			CreateTopicResult topicResult = getClient().createTopic(topicName);
			getClient().subscribe(new SubscribeRequest(topicResult.getTopicArn(), EMAIL_PROTOCOL, email));
		}
	}
}
