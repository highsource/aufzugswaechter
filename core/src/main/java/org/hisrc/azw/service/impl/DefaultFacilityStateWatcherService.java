package org.hisrc.azw.service.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.event.FacilityStateChangedEvent;
import org.hisrc.azw.event.FacilityStateChangedEventListener;
import org.hisrc.azw.integration.FacilityStateReportDataAccess;
import org.hisrc.azw.integration.StationDataAccess;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityStateReport;
import org.hisrc.azw.model.FacilityStateSnapshot;
import org.hisrc.azw.model.Station;
import org.hisrc.azw.service.FacilityService;
import org.hisrc.azw.service.FacilityStateSnapshotService;
import org.hisrc.azw.service.FacilityStateWatcherService;
import org.hisrc.azw.service.StationService;

public class DefaultFacilityStateWatcherService implements FacilityStateWatcherService {

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private ScheduledFuture<?> scheduledFuture;

	// 5 seconds
	private long initialDelay = 5 * 1000;

	// 30 seconds
	private long period = 60 * 1000;

	private Collection<FacilityStateChangedEventListener> listeners = Collections
			.synchronizedCollection(new LinkedList<FacilityStateChangedEventListener>());

	private StationDataAccess stationDataAccess;
	private FacilityStateReportDataAccess facilityStateReportDataAccess;

	private FacilityService facilityService;
	private StationService stationService;
	private FacilityStateSnapshotService facilityStateSnapshotService;

	public long getInitialDelay() {
		return initialDelay;
	}

	@SuppressWarnings("unused")
	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	public long getPeriod() {
		return period;
	}

	@SuppressWarnings("unused")
	public void setPeriod(long period) {
		this.period = period;
	}

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

	public DefaultFacilityStateWatcherService(StationDataAccess stationDataAccess,
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

	private DefaultFacilityStateWatcherService() {

	}

	@Override
	public void registerEventListener(FacilityStateChangedEventListener eventListener) {
		Validate.notNull(eventListener);
		this.listeners.add(eventListener);
	}

	private void fireEvent(final FacilityStateChangedEvent event) {
		synchronized (this.listeners) {
			for (FacilityStateChangedEventListener listener : this.listeners) {
				listener.stateChanged(event);
			}
		}
	}

	public void start() throws IllegalStateException {
		synchronized (this.scheduler) {
			if (this.scheduledFuture != null) {
				// TODO log
				// throw new
				// IllegalStateException("Service was already started.");
			} else {
				this.scheduledFuture = this.scheduler.scheduleAtFixedRate(new Runnable() {

					@Override
					public void run() {
						DefaultFacilityStateWatcherService.this.processFacilityStateReports();
					}
				}, getInitialDelay(), getPeriod(), TimeUnit.MILLISECONDS);
			}
		}
	}

	@Override
	public void stop() throws IllegalStateException {
		synchronized (this.scheduler) {
			if (this.scheduledFuture == null) {
				// TODO log
				// throw new
				// IllegalStateException("Service was not yet started.");
			} else {
				this.scheduledFuture.cancel(true);
				this.scheduledFuture = null;
			}
		}
	}

	private void processFacilityStateReports() {
		try {
			final long timestamp = System.currentTimeMillis();
			System.out.println("Checking facilities at [" + timestamp + "].");
			final Iterable<FacilityStateReport> reports = getFacilityStateReportDataAccess().findAll();

			for (FacilityStateReport report : reports) {
				try {
					processFacilityStateReport(timestamp, report);
				} catch (IOException ioex) {
					// TODO log
					ioex.printStackTrace();
				}
			}
		} catch (IOException ioex) {
			// TODO log
			ioex.printStackTrace();
		}
	}

	private void processFacilityStateReport(long timestamp, FacilityStateReport report) throws IOException {
		Facility facility = report.getFacility();

		final Long equipmentnumber = facility.getEquipmentnumber();

		final Facility existingFacility = getFacilityService().findByEquipmentnumber(equipmentnumber);

		if (existingFacility != null) {
			facility = existingFacility;
		} else {
			getFacilityService().persistOrUpdate(facility);
		}

		final FacilityStateSnapshot newSnapshot = new FacilityStateSnapshot(equipmentnumber, report.getFacilityState(),
				timestamp);

		final FacilityStateSnapshot oldSnapshot = getFacilityStateSnapshotService()
				.findLastByEquipmentnumber(equipmentnumber);

		final Long stationnumber = facility.getStationnumber();
		Station station = getStationService().findByStationnumber(stationnumber);
		if (station == null) {
			station = getStationDataAccess().findByStationnumber(stationnumber);
			if (station != null) {
				System.out.println(MessageFormat.format("Persisting station [{0}].", station));
				getStationService().persistOrUpdate(station);
			} else {
				station = Station.UNKNOWN;
			}
		}

		if (oldSnapshot == null || oldSnapshot.getState() != newSnapshot.getState()) {

			getFacilityStateSnapshotService().persistOrUpdate(newSnapshot);

			final FacilityStateChangedEvent event = new FacilityStateChangedEvent(station, facility, oldSnapshot,
					newSnapshot);

			fireEvent(event);
		}
	}
}
