package org.hisrc.azw.service.tests;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.hisrc.azw.event.FacilityStateChangedEvent;
import org.hisrc.azw.event.FacilityStateChangedEventListener;
import org.hisrc.azw.integration.FacilityStateReportDataAccess;
import org.hisrc.azw.integration.StationDataAccess;
import org.hisrc.azw.integration.impl.v_1_0.DefaultApiFacilityStateReportDataAccess;
import org.hisrc.azw.integration.impl.v_1_0.DefaultApiStationDataAccess;
import org.hisrc.azw.service.FacilityService;
import org.hisrc.azw.service.FacilityStateChangedEventService;
import org.hisrc.azw.service.FacilityStateSnapshotService;
import org.hisrc.azw.service.FacilityStateWatcherService;
import org.hisrc.azw.service.StationService;
import org.hisrc.azw.service.impl.DefaultFacilityStateChangedEventService;
import org.hisrc.azw.service.impl.DefaultFacilityStateWatcherService;
import org.hisrc.azw.service.impl.MemoryBasedFacilityService;
import org.hisrc.azw.service.impl.MemoryBasedFacilityStateSnapshotService;
import org.hisrc.azw.service.impl.MemoryBasedStationService;
import org.hisrc.fasta.client.v1.api.DefaultApi;
import org.hisrc.fasta.client.v1.api.DefaultApiFactory;


public class RunFacilityStateWatcherService {

	public static void main(String[] args) {

		final RunFacilityStateWatcherService runner = new RunFacilityStateWatcherService();
		runner.start();
	}
	
	private Properties properties = new Properties();
	{
		try (InputStream is = getClass().getResourceAsStream("/fasta.properties")) {
			properties.load(is);
		} catch (IOException ioex) {
			throw new ExceptionInInitializerError(ioex);
		}
	}

	private final DefaultApi api;
	private final StationDataAccess stationDataAccess;
	private final FacilityStateReportDataAccess facilityStateSnapshotDataAccess;
	private final FacilityService facilityService;
	private final StationService stationService;
	private final FacilityStateSnapshotService facilityStateSnapshotService;
	private final FacilityStateChangedEventService facilityStateChangedEventService;
	private final FacilityStateWatcherService facilityStateWatcherService;
	private final FacilityStateChangedEventListener newFacilitiesListener;
	private final FacilityStateChangedEventListener stateChangedFacilitiesListener;

	public RunFacilityStateWatcherService() {
		this.api = new DefaultApiFactory().createApi(properties.getProperty("fasta.accessToken"));
		this.stationDataAccess = new DefaultApiStationDataAccess(this.api);
		this.facilityStateSnapshotDataAccess = new DefaultApiFacilityStateReportDataAccess(this.api);

		this.facilityService = new MemoryBasedFacilityService();
		this.stationService = new MemoryBasedStationService();
		this.facilityStateSnapshotService = new MemoryBasedFacilityStateSnapshotService();
		this.facilityStateChangedEventService = new DefaultFacilityStateChangedEventService(stationDataAccess,
				facilityStateSnapshotDataAccess, facilityService, stationService, facilityStateSnapshotService);

		this.facilityStateWatcherService = new DefaultFacilityStateWatcherService(facilityStateSnapshotDataAccess,
				facilityStateSnapshotService, facilityStateChangedEventService);

		this.newFacilitiesListener = new FacilityStateChangedEventListener() {

			@Override
			public void stateChanged(FacilityStateChangedEvent e) {
				if (e.getOldSnapshot() == null) {
					System.out.println(MessageFormat.format("Station:\n{0}\nFacility:\n{1}\nState:\n{2}", e.getSource(),
							e.getFacility(), e.getNewSnapshot().getState()));
				}
			}
		};
		this.stateChangedFacilitiesListener = new FacilityStateChangedEventListener() {

			@Override
			public void stateChanged(FacilityStateChangedEvent e) {
				if (e.getOldSnapshot() != null) {
					System.out.println(MessageFormat.format(
							"Station:\n{0}\nFacility:\n{1}\nOld state:\n{2}\nNew state:\n{3}\nTimestamp:\n{4}",
							e.getSource(), e.getFacility(), e.getOldSnapshot().getState(),
							e.getNewSnapshot().getState(), e.getNewSnapshot().getTimestamp()));
				}
			}
		};
		facilityStateChangedEventService.registerEventListener(newFacilitiesListener);
		facilityStateChangedEventService.registerEventListener(stateChangedFacilitiesListener);
	}

	private void start() {
		facilityStateWatcherService.start();
	}
}
