package org.hisrc.azw.service.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.service.FacilityStateService;
import org.hisrc.azw.service.FacilityStateWatcherService;
import org.hisrc.dbeac.client.v_1_0.api.DefaultApi;
import org.hisrc.dbeac.client.v_1_0.invoker.ApiException;
import org.hisrc.dbeac.client.v_1_0.model.Facility;

public class DefaultFacilityStateWatcherService implements
		FacilityStateWatcherService {

	private DefaultApi api;

	private FacilityStateService facilityStateService;

	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	private ScheduledFuture<?> scheduledFuture;

	// 5 seconds
	private long initialDelay = 5 * 1000;

	// 30 seconds
	private long period = 60 * 1000;

	@SuppressWarnings("unused")
	private DefaultFacilityStateWatcherService() {
	}

	public DefaultFacilityStateWatcherService(DefaultApi api,
			FacilityStateService facilityStateService) {
		Validate.notNull(api);
		Validate.notNull(facilityStateService);
		this.api = api;
		this.facilityStateService = facilityStateService;
	}

	public DefaultApi getApi() {
		return api;
	}

	@SuppressWarnings("unused")
	private void setApi(DefaultApi api) {
		this.api = api;
	}

	public FacilityStateService getFacilityStateService() {
		return facilityStateService;
	}

	@SuppressWarnings("unused")
	private void setFacilityStateService(
			FacilityStateService facilityStateService) {
		this.facilityStateService = facilityStateService;
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	private void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	public long getPeriod() {
		return period;
	}

	private void setPeriod(long period) {
		this.period = period;
	}

	private void checkState() {
		try {
			System.out.println(MessageFormat.format("Checking state at [{0}].",
					System.currentTimeMillis()));
			final List<Facility> facilities = api.findFacilities(null, null);
			for (Facility facility : facilities) {
				facilityStateService.updateFacilityState(facility);
			}
		} catch (ApiException apiex) {
			// TODO log
			apiex.printStackTrace();
		}
	}

	public void start() throws IllegalStateException {
		synchronized (this.scheduler) {
			if (this.scheduledFuture != null) {
				// TODO log
				// throw new
				// IllegalStateException("Service was already started.");
			} else {
				this.scheduledFuture = this.scheduler.scheduleAtFixedRate(
						new Runnable() {

							@Override
							public void run() {
								DefaultFacilityStateWatcherService.this
										.checkState();
							}
						},

						getInitialDelay(), getPeriod(), TimeUnit.MILLISECONDS);
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
}
