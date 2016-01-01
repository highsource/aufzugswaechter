package org.hisrc.azw.integration.impl.v_1_0.tests;

import java.io.IOException;

import org.hisrc.azw.integration.StationDataAccess;
import org.hisrc.azw.integration.impl.v_1_0.DefaultApiStationDataAccess;
import org.hisrc.dbeac.client.v_1_0.api.DefaultApi;
import org.junit.Assert;
import org.junit.Test;

public class DefaultApiStationDataAccessTest {

	private StationDataAccess sut = new DefaultApiStationDataAccess(
			new DefaultApi());

	@Test
	public void findsByStationnumber() throws IOException {
		Assert.assertNotNull(sut.findByStationnumber(3925));
	}

	@Test
	public void findsNullByUnknownStationnumber() throws IOException {
		Assert.assertNull(sut.findByStationnumber(0));
	}
}
