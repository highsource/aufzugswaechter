package org.hisrc.azw.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.event.FacilityStateChangedEvent;
import org.hisrc.azw.event.FacilityStateChangedEventListener;
import org.hisrc.azw.service.FacilityStateService;
import org.hisrc.dbeac.client.v_1_0.api.DefaultApi;
import org.hisrc.dbeac.client.v_1_0.invoker.ApiException;
import org.hisrc.dbeac.client.v_1_0.model.Facility;
import org.hisrc.dbeac.client.v_1_0.model.Station;

public class MemoryBasedFacilityStateService implements FacilityStateService {

	private DefaultApi api;

	private Collection<FacilityStateChangedEventListener> listeners = Collections
			.synchronizedCollection(new LinkedList<FacilityStateChangedEventListener>());

	private Map<Long, Facility> facilities = Collections
			.synchronizedMap(new HashMap<Long, Facility>());

	public DefaultApi getApi() {
		return api;
	}

	@SuppressWarnings("unused")
	private void setApi(DefaultApi api) {
		this.api = api;
	}

	@SuppressWarnings("unused")
	private MemoryBasedFacilityStateService() {
	}

	public MemoryBasedFacilityStateService(DefaultApi api) {
		this.api = api;
	}

	@Override
	public void registerEventListener(
			FacilityStateChangedEventListener eventListener) {
		Validate.notNull(eventListener);
		this.listeners.add(eventListener);
	}

	@Override
	public void updateFacilityState(Facility newFacility) {
		Validate.notNull(newFacility);
		final Long en = newFacility.getEquipmentnumber();
		Validate.notNull(en);
		final Facility oldFacility = this.facilities.get(en);
		if (oldFacility == null
				|| oldFacility.getState() != newFacility.getState()) {
			this.facilities.put(en, newFacility);
			createAndFireEvent(oldFacility, newFacility);
		}
	}

	private void createAndFireEvent(Facility oldFacility, Facility newFacility) {
		Long sn = newFacility.getStationnumber();
		if (sn != null) {
			try {
				final Station station = getApi().findStationByStationNumber(sn);
				final FacilityStateChangedEvent event = new FacilityStateChangedEvent(
						station, oldFacility, newFacility);
				fireEvent(event);
			} catch (ApiException apiex) {
				// TODO log
			}
		} else {
			// TODO log
		}
	}

	private void fireEvent(final FacilityStateChangedEvent event) {
		synchronized (this.listeners) {
			for (FacilityStateChangedEventListener listener : this.listeners) {
				listener.stateChanged(event);
			}
		}
	}
}
