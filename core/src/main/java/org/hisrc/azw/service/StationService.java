package org.hisrc.azw.service;

import org.hisrc.azw.model.Station;

public interface StationService {

	public Iterable<Station> findAll();

	public Station findByStationnumber(long stationnumber);

	public void persistOrUpdate(Station station);
}
