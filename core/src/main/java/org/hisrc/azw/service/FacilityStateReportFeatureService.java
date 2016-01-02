package org.hisrc.azw.service;

import java.util.List;

import org.hisrc.azw.dto.FacilityStateReportFeature;
import org.hisrc.azw.model.FacilityState;

public interface FacilityStateReportFeatureService {

	public List<FacilityStateReportFeature> findAll();

	public List<FacilityStateReportFeature> findByFacilityStates(
			Iterable<FacilityState> facilityStates);

	public List<FacilityStateReportFeature> findAllUpdatedSince(
			long timestamp);

	public List<FacilityStateReportFeature> findByFacilityStatesUpdatedSince(
			Iterable<FacilityState> facilityStates, long timestamp);

	public FacilityStateReportFeature findByFacilityEquipmentnumber(
			long facilityEquipmentnumber);
}
