package org.hisrc.azw.twitter.impl;

import java.text.MessageFormat;

import javax.inject.Inject;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.Validate;
import org.hisrc.azw.event.FacilityStateChangedEvent;
import org.hisrc.azw.event.FacilityStateChangedEventListener;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.Station;

public class CamelFacilityStateChangedEventListener implements FacilityStateChangedEventListener {

	@Inject
	private ProducerTemplate producerTemplate;

	public ProducerTemplate getProducerTemplate() {
		return producerTemplate;
	}

	public void setProducerTemplate(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}

	@Override
	public void stateChanged(FacilityStateChangedEvent event) {
		Validate.notNull(event);
		// Filter
		if (event.getOldSnapshot() != null) {
			// Transform
			final Station station = event.getSource();
			final Facility facility = event.getFacility();
			final FacilityState oldFacilityState = event.getOldSnapshot().getState();
			final FacilityState newFacilityState = event.getNewSnapshot().getState();
			final String message = MessageFormat.format("Facility {0} / {1} has changed state from {2} to {3}",
					station.getName(), facility.getDescription(), oldFacilityState, newFacilityState);
			// Split and send
			try {
				getProducerTemplate().sendBody("direct:laconic", message);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
	}

}
