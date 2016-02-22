package org.hisrc.azw.camel.tests;

import javax.inject.Inject;

import org.apache.camel.ProducerTemplate;
import org.hisrc.azw.event.FacilityStateChangedEvent;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateSnapshot;
import org.hisrc.azw.model.FacilityType;
import org.hisrc.azw.model.Station;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/org/hisrc/azw/camel/config.xml")
public class RunDynamicSnsEndpoint {

	@Inject
	private ProducerTemplate template;

	@Test
	public void test() throws Exception {

		final Station station = new Station(1L, "No name");

		final Facility facility = new Facility(1L, FacilityType.ELEVATOR, "No description", null, 1L);

		final FacilityStateSnapshot newSnapshot = new FacilityStateSnapshot(1L, FacilityState.ACTIVE, 1L);

		final FacilityStateChangedEvent event = new FacilityStateChangedEvent(station, facility, null, newSnapshot);

		template.sendBody("direct:laconic", event);
	}

}
