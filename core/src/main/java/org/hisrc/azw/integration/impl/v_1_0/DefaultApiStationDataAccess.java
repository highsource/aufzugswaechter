package org.hisrc.azw.integration.impl.v_1_0;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.integration.StationDataAccess;
import org.hisrc.azw.model.Station;
import org.hisrc.dbeac.client.v_1_0.api.DefaultApi;
import org.hisrc.dbeac.client.v_1_0.invoker.ApiException;

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

	@SuppressWarnings("unused")
	private void setApi(DefaultApi api) {
		this.api = api;
	}

	@Override
	public Station findByStationnumber(long stationnumber) throws IOException {
		try {
			org.hisrc.dbeac.client.v_1_0.model.Station s = getApi()
					.findStationByStationNumber(stationnumber);
			return asStation(s);
		} catch (ApiException cause) {
			// throw new
			// IOException(MessageFormat.format("Error retrieving station with number [{0}].",
			// stationnumber), cause);
			return null;
		}
	}

	private Station asStation(org.hisrc.dbeac.client.v_1_0.model.Station s) {
		final Station station = new Station(s.getStationnumber(), s.getName());
		return station;
	}

}
