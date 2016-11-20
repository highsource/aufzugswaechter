package org.hisrc.azw.integration.impl.v_1_0.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.hisrc.azw.integration.FacilityStateReportDataAccess;
import org.hisrc.azw.integration.impl.v_1_0.DefaultApiFacilityStateReportDataAccess;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateReport;
import org.hisrc.fasta.client.v1.api.DefaultApiFactory;
import org.junit.Assert;
import org.junit.Test;

public class DefaultApiFacilityStateReportDataAccessTest {

	private Properties properties = new Properties();
	{
		try (InputStream is = getClass().getResourceAsStream("/fasta.properties")) {
			properties.load(is);
		} catch (IOException ioex) {
			throw new ExceptionInInitializerError(ioex);
		}
	}

	private FacilityStateReportDataAccess sut = new DefaultApiFacilityStateReportDataAccess(
			new DefaultApiFactory().createApi(properties.getProperty("fasta.accessToken")));

	@Test
	public void findsByEquipmentnumber() throws IOException {
		final FacilityStateReport f = sut.findByEquipmentnumber(10441811L);
		Assert.assertNotNull(f);
	}

	@Test
	public void findsNullByUnknownEquipmentnumber() throws IOException {
		Assert.assertNull(sut.findByEquipmentnumber(0));
	}

	@Test
	public void findsInactive() throws IOException {
		Assert.assertNotNull(sut.findByFacilityStates(Arrays.asList(FacilityState.INACTIVE)));
	}
}
