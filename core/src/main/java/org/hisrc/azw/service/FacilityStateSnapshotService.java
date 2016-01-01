package org.hisrc.azw.service;

import org.hisrc.azw.model.FacilityStateSnapshot;

public interface FacilityStateSnapshotService {

	public FacilityStateSnapshot findLastByEquipmentnumber(long equipmentnumber);

	public void persistOrUpdate(FacilityStateSnapshot snapshot);

}
