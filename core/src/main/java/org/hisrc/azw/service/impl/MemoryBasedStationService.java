package org.hisrc.azw.service.impl;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.hisrc.azw.model.Station;
import org.hisrc.azw.service.StationService;

public class MemoryBasedStationService implements StationService {

	private final Map<Long, Station> stations = Collections
			.synchronizedMap(new TreeMap<Long, Station>());

	@Override
	public Iterable<Station> findAll() {
		return stations.values();
	}

	@Override
	public Station findByStationnumber(long stationnumber) {
		return stations.get(stationnumber);
	}

	@Override
	public void persistOrUpdate(Station station) {
		stations.put(station.getStationnumber(), station);
	}
}
