package org.hisrc.azw.camel.gcm;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hisrc.azw.event.FacilityStateChangedEvent;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityStateSnapshot;
import org.hisrc.azw.model.FacilityType;
import org.hisrc.azw.model.Station;
import org.hisrc.camel.component.gcm.GcmConstants;

public class FacilityStateChangedEventToGcmPayloadProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		final FacilityStateChangedEvent event = exchange.getIn().getBody(FacilityStateChangedEvent.class);

		if (event != null) {
			final Map<String, String> data = new HashMap<String, String>();
			final Station station = event.getSource();
			data.put("stationnumber", station.getStationnumber().toString());
			data.put("stationname", station.getName());
			final Facility facility = event.getFacility();
			final String facilityDescription = facility.getDescription();
			if (facilityDescription != null) {
				data.put("facilityDescription", facilityDescription);
			}
			final Long facilityEquipmentnumber = facility.getEquipmentnumber();
			data.put("facilityEquipmentnumber", facilityEquipmentnumber.toString());
			final FacilityType facilityType = facility.getType();
			data.put("facilityType", facilityType.name());

			final FacilityStateSnapshot oldSnapshot = event.getOldSnapshot();
			if (oldSnapshot != null) {
				data.put("oldFacilityState", oldSnapshot.getState().name());
				data.put("oldFacilityStateKnownSince", oldSnapshot.getTimestamp().toString());
			}
			final FacilityStateSnapshot newSnapshot = event.getNewSnapshot();
			data.put("newFacilityState", newSnapshot.getState().name());
			data.put("newFacilityStateKnownSince", newSnapshot.getTimestamp().toString());
			final String topic = MessageFormat.format("/topics/facility-{0,number,#}", facilityEquipmentnumber);
			exchange.getIn().setHeader(GcmConstants.TO, topic);
			exchange.getIn().setBody(data);
		}
	}

}
