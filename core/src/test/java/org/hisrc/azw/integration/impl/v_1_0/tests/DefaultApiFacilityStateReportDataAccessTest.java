package org.hisrc.azw.integration.impl.v_1_0.tests;

import java.io.IOException;
import java.util.Arrays;

import org.hisrc.azw.integration.FacilityStateReportDataAccess;
import org.hisrc.azw.integration.impl.v_1_0.DefaultApiFacilityStateReportDataAccess;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateReport;
import org.hisrc.dbeac.client.v_1_0.api.DefaultApi;
import org.junit.Assert;
import org.junit.Test;

public class DefaultApiFacilityStateReportDataAccessTest {

	private FacilityStateReportDataAccess sut = new DefaultApiFacilityStateReportDataAccess(
			new DefaultApi());

	@Test
	public void findsByEquipmentnumber() throws IOException {
		final FacilityStateReport f = sut.findByEquipmentnumber(10110110L);
		Assert.assertNotNull(f);
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
