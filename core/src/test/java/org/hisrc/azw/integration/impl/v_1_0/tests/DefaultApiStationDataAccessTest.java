package org.hisrc.azw.integration.impl.v_1_0.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hisrc.azw.integration.StationDataAccess;
import org.hisrc.azw.integration.impl.v_1_0.DefaultApiStationDataAccess;
import org.hisrc.fasta.client.v1.api.DefaultApiFactory;
import org.junit.Assert;
import org.junit.Test;

public class DefaultApiStationDataAccessTest {
	
	private Properties properties = new Properties();
	{
		try(InputStream is = getClass().getResourceAsStream("/fasta.properties"))
		{
			properties.load(is);
		}
		catch(IOException ioex) {
			throw new ExceptionInInitializerError(ioex);
		}
	}

	private StationDataAccess sut = new DefaultApiStationDataAccess(
			new DefaultApiFactory().createApi(properties.getProperty("fasta.accessToken")));

	@Test
	public void findsByStationnumber() throws IOException {
		Assert.assertNotNull(sut.findByStationnumber(530));
	}

	@Test
	public void findsNullByUnknownStationnumber() throws IOException {
		Assert.assertNull(sut.findByStationnumber(0));
	}
}
