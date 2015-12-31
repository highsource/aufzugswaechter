package org.hisrc.azw.service;

import org.hisrc.azw.event.FacilityStateChangedEventListener;
import org.hisrc.dbeac.client.v_1_0.model.Facility;

public interface FacilityStateService {

	void updateFacilityState(Facility facility);

	void registerEventListener(FacilityStateChangedEventListener eventListener);
}
