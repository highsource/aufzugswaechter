package org.hisrc.azw.integration;

import java.io.IOException;

import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityState;

public interface FacilityDataAccess {

	public Iterable<Facility> findAll() throws IOException;

	public Facility findByEquipmentnumber(long equipmentnumber)
			throws IOException;

	public Iterable<Facility> findByFacilityStates(
			Iterable<FacilityState> facilityStats) throws IOException;
}
