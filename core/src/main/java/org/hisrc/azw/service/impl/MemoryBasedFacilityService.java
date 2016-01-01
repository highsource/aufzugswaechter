package org.hisrc.azw.service.impl;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.hisrc.azw.model.Facility;
import org.hisrc.azw.service.FacilityService;

public class MemoryBasedFacilityService implements FacilityService {

	private final Map<Long, Facility> facilities = Collections
			.synchronizedMap(new TreeMap<Long, Facility>());

	@Override
	public Iterable<Facility> findAll() {
		return facilities.values();
	}

	@Override
	public Facility findByEquipmentnumber(long equipmentnumber) {
		return facilities.get(equipmentnumber);
	}

	@Override
	public void persistOrUpdate(Facility facility) {
		facilities.put(facility.getEquipmentnumber(), facility);
	}
}
