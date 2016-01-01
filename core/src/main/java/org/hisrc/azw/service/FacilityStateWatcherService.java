package org.hisrc.azw.service;

import org.hisrc.azw.event.FacilityStateChangedEventListener;

public interface FacilityStateWatcherService {

	public void registerEventListener(
			FacilityStateChangedEventListener eventListener);

	public void start();

	public void stop();

}
