package org.hisrc.azw.service;

import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateSnapshot;

public interface FacilityStateSnapshotService {

	public void persistOrUpdate(FacilityStateSnapshot snapshot);

	public FacilityStateSnapshot findLastByEquipmentnumber(long equipmentnumber);

	public Iterable<FacilityStateSnapshot> findLastByFacilityStates(
			Iterable<FacilityState> facilityStates);

	public Iterable<FacilityStateSnapshot> findAllLast();

	public Iterable<FacilityStateSnapshot> findAllLastUpdatedSince(
			long timestamp);

	public Iterable<FacilityStateSnapshot> findLastByFacilityStatesUpdatedSince(
			Iterable<FacilityState> facilityStates, long timestamp);

}
