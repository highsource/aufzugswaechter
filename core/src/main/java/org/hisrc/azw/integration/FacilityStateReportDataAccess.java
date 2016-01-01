package org.hisrc.azw.integration;

import java.io.IOException;

import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateReport;

public interface FacilityStateReportDataAccess {

	public Iterable<FacilityStateReport> findAll() throws IOException;

	public FacilityStateReport findByEquipmentnumber(long equipmentnumber)
			throws IOException;

	public Iterable<FacilityStateReport> findByFacilityStates(
			Iterable<FacilityState> facilityStats) throws IOException;
}
