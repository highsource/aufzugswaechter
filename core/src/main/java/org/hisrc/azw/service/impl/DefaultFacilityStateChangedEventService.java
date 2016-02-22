package org.hisrc.azw.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.event.FacilityStateChangedEvent;
import org.hisrc.azw.event.FacilityStateChangedEventListener;
import org.hisrc.azw.integration.FacilityStateReportDataAccess;
import org.hisrc.azw.integration.StationDataAccess;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateReport;
import org.hisrc.azw.model.FacilityStateSnapshot;
import org.hisrc.azw.model.Station;
import org.hisrc.azw.service.FacilityService;
import org.hisrc.azw.service.FacilityStateChangedEventService;
import org.hisrc.azw.service.FacilityStateSnapshotService;
import org.hisrc.azw.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFacilityStateChangedEventService implements FacilityStateChangedEventService {

	private final static Logger LOG = LoggerFactory.getLogger(DefaultFacilityStateChangedEventService.class);

	private Collection<FacilityStateChangedEventListener> listeners = Collections
			.synchronizedCollection(new LinkedList<FacilityStateChangedEventListener>());

	private StationDataAccess stationDataAccess;
	private FacilityStateReportDataAccess facilityStateReportDataAccess;

	private FacilityService facilityService;
	private StationService stationService;
	private FacilityStateSnapshotService facilityStateSnapshotService;

	public StationDataAccess getStationDataAccess() {
		return stationDataAccess;
	}

	public FacilityStateReportDataAccess getFacilityStateReportDataAccess() {
		return facilityStateReportDataAccess;
	}

	public FacilityStateSnapshotService getFacilityStateSnapshotService() {
		return facilityStateSnapshotService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public StationService getStationService() {
		return stationService;
	}

	public void setStationDataAccess(StationDataAccess stationDataAccess) {
		this.stationDataAccess = stationDataAccess;
	}

	public void setFacilityStateReportDataAccess(FacilityStateReportDataAccess facilityStateReportDataAccess) {
		this.facilityStateReportDataAccess = facilityStateReportDataAccess;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public void setStationService(StationService stationService) {
		this.stationService = stationService;
	}

	public void setFacilityStateSnapshotService(FacilityStateSnapshotService facilityStateSnapshotService) {
		this.facilityStateSnapshotService = facilityStateSnapshotService;
	}

	public DefaultFacilityStateChangedEventService(StationDataAccess stationDataAccess,
			FacilityStateReportDataAccess facilityStateReportDataAccess, FacilityService facilityService,
			StationService stationService, FacilityStateSnapshotService facilityStateSnapshotService) {
		Validate.notNull(stationDataAccess);
		Validate.notNull(facilityStateReportDataAccess);
		Validate.notNull(facilityService);
		Validate.notNull(stationService);
		Validate.notNull(facilityStateSnapshotService);
		this.stationDataAccess = stationDataAccess;
		this.facilityStateReportDataAccess = facilityStateReportDataAccess;
		this.facilityService = facilityService;
		this.stationService = stationService;
		this.facilityStateSnapshotService = facilityStateSnapshotService;
	}

	private DefaultFacilityStateChangedEventService() {

	}

	@Override
	public void registerEventListener(FacilityStateChangedEventListener eventListener) {
		Validate.notNull(eventListener);
		this.listeners.add(eventListener);
	}

	private void fireEvent(final FacilityStateChangedEvent event) {
		synchronized (this.listeners) {
			for (FacilityStateChangedEventListener listener : this.listeners) {
				try {
					listener.stateChanged(event);
				} catch (Exception ex) {
					LOG.error("Facility state changed event listener has thrown an exception.", ex);
				}
			}
		}
	}

	public void processFacilityStateReports(final Iterable<FacilityStateReport> reports) {
		final long timestamp = System.currentTimeMillis();
		for (FacilityStateReport report : reports) {
			processFacilityStateReport(timestamp, report);
		}
	}

	private void processFacilityStateReport(long timestamp, FacilityStateReport report) {
		Facility facility = report.getFacility();

		final Long equipmentnumber = facility.getEquipmentnumber();

		final Facility existingFacility = getFacilityService().findByEquipmentnumber(equipmentnumber);

		if (existingFacility != null) {
			facility = existingFacility;
		} else {
			getFacilityService().persistOrUpdate(facility);
		}

		final FacilityState newFacilityState = report.getFacilityState();

		final FacilityStateSnapshot newSnapshot = new FacilityStateSnapshot(equipmentnumber, newFacilityState,
				timestamp);

		final FacilityStateSnapshot oldSnapshot = getFacilityStateSnapshotService()
				.findLastByEquipmentnumber(equipmentnumber);

		final Long stationnumber = facility.getStationnumber();
		Station station = getStationService().findByStationnumber(stationnumber);
		if (station == null) {
			try {
				station = getStationDataAccess().findByStationnumber(stationnumber);
			} catch (IOException ioex) {
				station = null;
				LOG.error("Could not retrieve station by number.", ioex);
			}
			if (station != null) {
				getStationService().persistOrUpdate(station);
			} else {
				station = Station.UNKNOWN;
			}
		}

		if (oldSnapshot == null || oldSnapshot.getState() != newSnapshot.getState()) {
			LOG.trace("Facility [{}] has changed state from [{}] to [{}].", facility,
					oldSnapshot == null ? null : oldSnapshot.getState(), newSnapshot.getState());
			getFacilityStateSnapshotService().persistOrUpdate(newSnapshot);

			final FacilityStateChangedEvent event = new FacilityStateChangedEvent(station, facility, oldSnapshot,
					newSnapshot);
			fireEvent(event);
		}
	}
}
