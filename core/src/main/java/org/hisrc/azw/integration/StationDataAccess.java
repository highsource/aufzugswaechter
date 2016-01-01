package org.hisrc.azw.integration;

import java.io.IOException;

import org.hisrc.azw.model.Station;

public interface StationDataAccess {

	public Station findByStationnumber(long stationnumber) throws IOException;
}
