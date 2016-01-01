package org.hisrc.azw.integration.impl.v_1_0.tests;

import java.io.IOException;
import java.util.Arrays;

import org.hisrc.azw.integration.FacilityDataAccess;
import org.hisrc.azw.integration.impl.v_1_0.DefaultApiFacilityDataAccess;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.dbeac.client.v_1_0.api.DefaultApi;
import org.junit.Assert;
import org.junit.Test;

public class DefaultApiFacilityDataAccessTest {

	private FacilityDataAccess sut = new DefaultApiFacilityDataAccess(
			new DefaultApi());

	@Test
	public void findsByEquipmentnumber() throws IOException {
		final Facility facility = sut.findByEquipmentnumber(10110110L);
		Assert.assertNotNull(facility);
	}

	@Test
	public void findsNullByUnknownEquipmentnumber() throws IOException {
		Assert.assertNull(sut.findByEquipmentnumber(0));
	}

	@Test
	public void findsInactive() throws IOException {
		Assert.assertNotNull(sut.findByFacilityStates(Arrays
				.asList(FacilityState.INACTIVE)));
	}
}
