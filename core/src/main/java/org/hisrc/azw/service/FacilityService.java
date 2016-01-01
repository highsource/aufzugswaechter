package org.hisrc.azw.service;

import org.hisrc.azw.model.Facility;

public interface FacilityService {
	public Iterable<Facility> findAll();

	public Facility findByEquipmentnumber(long equipmentnumber);

	public void persistOrUpdate(Facility facility);
}
