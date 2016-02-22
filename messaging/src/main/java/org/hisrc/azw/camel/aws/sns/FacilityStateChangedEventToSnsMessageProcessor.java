package org.hisrc.azw.camel.aws.sns;

import java.text.MessageFormat;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.aws.sns.SnsConstants;
import org.hisrc.azw.event.FacilityStateChangedEvent;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.Station;
import org.hisrc.camel.component.aws.sns.DynamicSnsConstants;

public class FacilityStateChangedEventToSnsMessageProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		final FacilityStateChangedEvent event = exchange.getIn().getBody(FacilityStateChangedEvent.class);
		if (event != null) {
			// Transform
			final Station station = event.getSource();
			final Facility facility = event.getFacility();
			final FacilityState oldFacilityState = event.getOldSnapshot() == null ? null
					: event.getOldSnapshot().getState();
			final FacilityState newFacilityState = event.getNewSnapshot().getState();
			final String stationName = station.getName();
			final Long facilityEquipmentnumber = facility.getEquipmentnumber();
			final String facilityDescription = facility.getDescription() != null ? facility.getDescription()
					: MessageFormat.format("{0} {1,number,#}", facility.getType(), facilityEquipmentnumber);
			final String subject = MessageFormat.format("{0}: {1} is now {2}", stationName, facilityDescription,
					newFacilityState);
			final String message;
			if (oldFacilityState != null) {
				message = MessageFormat.format("{0}: {1} has changed state from {2} to {3}", stationName,
						facilityDescription, oldFacilityState, newFacilityState);
			} else {
				message = MessageFormat.format("{0}: {1} has changed state to {3}", stationName, facilityDescription,
						newFacilityState);
			}
			exchange.getIn().setHeader(SnsConstants.SUBJECT, subject);
			final String topic = MessageFormat.format("facility-{0,number,#}", facilityEquipmentnumber);
			exchange.getIn().setHeader(DynamicSnsConstants.TOPIC, topic);
			exchange.getIn().setBody(message);
		}
	}

}
