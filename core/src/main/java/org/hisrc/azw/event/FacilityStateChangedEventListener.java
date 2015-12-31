package org.hisrc.azw.event;

import java.util.EventListener;


public interface FacilityStateChangedEventListener extends EventListener{

	void stateChanged(FacilityStateChangedEvent e);
}
