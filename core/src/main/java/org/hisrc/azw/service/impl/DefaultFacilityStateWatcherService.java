package org.hisrc.azw.service.impl;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.integration.FacilityStateReportDataAccess;
import org.hisrc.azw.model.FacilityStateReport;
import org.hisrc.azw.service.FacilityStateChangedEventService;
import org.hisrc.azw.service.FacilityStateSnapshotService;
import org.hisrc.azw.service.FacilityStateWatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFacilityStateWatcherService implements FacilityStateWatcherService {

	private final static Logger LOG = LoggerFactory.getLogger(DefaultFacilityStateWatcherService.class);

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private ScheduledFuture<?> scheduledFuture;

	// 50 seconds
	private long initialDelay = 50 * 1000;

	// 60 seconds
	private long period = 60 * 1000;

	private FacilityStateChangedEventService facilityStateChangedEventService;

	private FacilityStateReportDataAccess facilityStateReportDataAccess;

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

	public FacilityStateReportDataAccess getFacilityStateReportDataAccess() {
		return facilityStateReportDataAccess;
	}
	
	public void setFacilityStateReportDataAccess(FacilityStateReportDataAccess facilityStateReportDataAccess) {
		this.facilityStateReportDataAccess = facilityStateReportDataAccess;
	}

	public FacilityStateSnapshotService getFacilityStateSnapshotService() {
		return facilityStateSnapshotService;
	}
	
	public void setFacilityStateSnapshotService(FacilityStateSnapshotService facilityStateSnapshotService) {
		this.facilityStateSnapshotService = facilityStateSnapshotService;
	}

	public FacilityStateChangedEventService getFacilityStateChangedEventService() {
		return facilityStateChangedEventService;
	}

	public void setFacilityStateChangedEventService(FacilityStateChangedEventService facilityStateChangedEventService) {
		this.facilityStateChangedEventService = facilityStateChangedEventService;
	}

	public DefaultFacilityStateWatcherService(FacilityStateReportDataAccess facilityStateReportDataAccess,
			FacilityStateSnapshotService facilityStateSnapshotService,
			FacilityStateChangedEventService facilityStateChangedEventService) {
		Validate.notNull(facilityStateReportDataAccess);
		Validate.notNull(facilityStateSnapshotService);
		Validate.notNull(facilityStateChangedEventService);
		this.facilityStateReportDataAccess = facilityStateReportDataAccess;
		this.facilityStateSnapshotService = facilityStateSnapshotService;
		this.facilityStateChangedEventService = facilityStateChangedEventService;
	}

	private DefaultFacilityStateWatcherService() {

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
						try {
							DefaultFacilityStateWatcherService.this.processFacilityStateReports();
						} catch (Exception ex) {
							LOG.error("Error during the regular check of the facility state reports.", ex);
						}
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
		LOG.trace("Checking facilities.");
		final Iterable<FacilityStateReport> reports;
		try {
			reports = getFacilityStateReportDataAccess().findAll();
			getFacilityStateChangedEventService().processFacilityStateReports(reports);
		} catch (IOException ioex) {
			LOG.warn("Could not retrieve current facility state reports.", ioex);
		}
	}
}
