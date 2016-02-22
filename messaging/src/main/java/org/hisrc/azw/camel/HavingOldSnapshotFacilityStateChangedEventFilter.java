package org.hisrc.azw.camel;

import org.apache.camel.Body;
import org.hisrc.azw.event.FacilityStateChangedEvent;

public class HavingOldSnapshotFacilityStateChangedEventFilter {
	public boolean hasOldSnapshot(@Body FacilityStateChangedEvent event) {
		return event != null && event.getOldSnapshot() != null;
	}
}
