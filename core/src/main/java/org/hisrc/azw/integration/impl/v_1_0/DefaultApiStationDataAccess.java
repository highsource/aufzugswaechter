package org.hisrc.azw.integration.impl.v_1_0;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.integration.StationDataAccess;
import org.hisrc.azw.model.Station;
import org.hisrc.fasta.client.v1.api.DefaultApi;
import org.hisrc.fasta.client.v1.invoker.ApiException;

public class DefaultApiStationDataAccess implements StationDataAccess {

	private DefaultApi api;

	@SuppressWarnings("unused")
	private DefaultApiStationDataAccess() {
	}

	public DefaultApiStationDataAccess(DefaultApi api) {
		Validate.notNull(api);
		this.api = api;
	}

	public DefaultApi getApi() {
		return api;
	}

	public void setApi(DefaultApi api) {
		this.api = api;
	}

	@Override
	public Station findByStationnumber(long stationnumber) throws IOException {
		try {
			org.hisrc.fasta.client.v1.model.Station s = getApi()
					.findStationByStationNumber(stationnumber);
			return asStation(s);
		} catch (ApiException cause) {
			// TODO distinguish not found vs. io ex
			return null;
		}
	}

	private Station asStation(org.hisrc.fasta.client.v1.model.Station s) {
		final Station station = new Station(s.getStationnumber(), s.getName());
		return station;
	}

}
