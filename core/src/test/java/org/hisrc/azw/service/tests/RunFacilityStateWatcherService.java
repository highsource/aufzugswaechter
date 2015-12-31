package org.hisrc.azw.service.tests;

import java.text.MessageFormat;

import org.hisrc.azw.event.FacilityStateChangedEvent;
import org.hisrc.azw.event.FacilityStateChangedEventListener;
import org.hisrc.azw.service.FacilityStateService;
import org.hisrc.azw.service.FacilityStateWatcherService;
import org.hisrc.azw.service.impl.DefaultFacilityStateWatcherService;
import org.hisrc.azw.service.impl.MemoryBasedFacilityStateService;
import org.hisrc.dbeac.client.v_1_0.api.DefaultApi;

public class RunFacilityStateWatcherService {

	public static void main(String[] args) {

		final RunFacilityStateWatcherService runner = new RunFacilityStateWatcherService();
		runner.start();
	}

	private final DefaultApi api;
	private final FacilityStateService facilityStateService;
	private final FacilityStateWatcherService facilityStateWatcherService;
	private final FacilityStateChangedEventListener newFacilitiesListener;
	private final FacilityStateChangedEventListener stateChangedFacilitiesListener;

	public RunFacilityStateWatcherService() {
		this.api = new DefaultApi();
		this.facilityStateService = new MemoryBasedFacilityStateService(api);
		this.facilityStateWatcherService = new DefaultFacilityStateWatcherService(
				api, facilityStateService);

		this.newFacilitiesListener = new FacilityStateChangedEventListener() {

			@Override
			public void stateChanged(FacilityStateChangedEvent e) {
				if (e.getOldValue() == null) {
					System.out.println(MessageFormat.format(
							"Station:\n{0}\nNew facility:\n{1}", e.getSource(),
							e.getNewValue()));
				}
			}
		};
		this.stateChangedFacilitiesListener = new FacilityStateChangedEventListener() {

			@Override
			public void stateChanged(FacilityStateChangedEvent e) {
				if (e.getOldValue() != null) {
					System.out
							.println(MessageFormat
									.format("Station:\n{0}\nOld facility:\n{1}\nNew facility:\n{2}",
											e.getSource(), e.getOldValue(),
											e.getNewValue()));
				}
			}
		};
		facilityStateService.registerEventListener(newFacilitiesListener);
		facilityStateService
				.registerEventListener(stateChangedFacilitiesListener);
	}

	private void start() {
		facilityStateWatcherService.start();
	}
}
