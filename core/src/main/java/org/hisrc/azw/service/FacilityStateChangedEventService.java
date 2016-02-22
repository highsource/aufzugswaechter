package org.hisrc.azw.service;

import org.hisrc.azw.event.FacilityStateChangedEventListener;
import org.hisrc.azw.model.FacilityStateReport;

public interface FacilityStateChangedEventService {
	
	public void processFacilityStateReports(Iterable<FacilityStateReport> reports);

	public void registerEventListener(
			FacilityStateChangedEventListener eventListener);

}
