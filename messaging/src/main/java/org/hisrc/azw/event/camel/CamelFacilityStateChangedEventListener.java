package org.hisrc.azw.event.camel;

import javax.inject.Inject;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.Validate;
import org.hisrc.azw.event.FacilityStateChangedEvent;
import org.hisrc.azw.event.FacilityStateChangedEventListener;

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
		if (getProducerTemplate().getCamelContext().getStatus().isStarted()) {
			getProducerTemplate().sendBody("direct:laconic", event);
		}
	}
}
