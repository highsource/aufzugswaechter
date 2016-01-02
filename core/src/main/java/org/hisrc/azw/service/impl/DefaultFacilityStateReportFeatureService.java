package org.hisrc.azw.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.dto.FacilityStateReportFeature;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateSnapshot;
import org.hisrc.azw.model.Station;
import org.hisrc.azw.service.FacilityService;
import org.hisrc.azw.service.FacilityStateSnapshotService;
import org.hisrc.azw.service.FacilityStateReportFeatureService;
import org.hisrc.azw.service.StationService;

public class DefaultFacilityStateReportFeatureService implements FacilityStateReportFeatureService {

	private StationService stationService;
	private FacilityService facilityService;
	private FacilityStateSnapshotService facilityStateSnapshotService;

	public StationService getStationService() {
		return stationService;
	}

	public void setStationService(StationService stationService) {
		this.stationService = stationService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public FacilityStateSnapshotService getFacilityStateSnapshotService() {
		return facilityStateSnapshotService;
	}

	public void setFacilityStateSnapshotService(FacilityStateSnapshotService facilityStateSnapshotService) {
		this.facilityStateSnapshotService = facilityStateSnapshotService;
	}

	@SuppressWarnings("unused")
	private DefaultFacilityStateReportFeatureService() {
	}

	public DefaultFacilityStateReportFeatureService(StationService stationService, FacilityService facilityService,
			FacilityStateSnapshotService facilityStateSnapshotService) {
		Validate.notNull(stationService);
		Validate.notNull(facilityService);
		Validate.notNull(facilityStateSnapshotService);
		this.stationService = stationService;
		this.facilityService = facilityService;
		this.facilityStateSnapshotService = facilityStateSnapshotService;
	}

	@Override
	public List<FacilityStateReportFeature> findAll() {
		final Iterable<FacilityStateSnapshot> snapshots = getFacilityStateSnapshotService().findAllLast();
		return createFeatures(snapshots);
	}

	@Override
	public List<FacilityStateReportFeature> findAllUpdatedSince(long timestamp) {
		final Iterable<FacilityStateSnapshot> snapshots = getFacilityStateSnapshotService()
				.findAllLastUpdatedSince(timestamp);
		return createFeatures(snapshots);
	}

	@Override
	public FacilityStateReportFeature findByFacilityEquipmentnumber(long facilityEquipmentnumber) {
		final FacilityStateSnapshot snapshot = getFacilityStateSnapshotService()
				.findLastByEquipmentnumber(facilityEquipmentnumber);
		return createFeature(snapshot);
	}

	@Override
	public List<FacilityStateReportFeature> findByFacilityStates(Iterable<FacilityState> facilityStates) {
		final Iterable<FacilityStateSnapshot> snapshots = getFacilityStateSnapshotService()
				.findLastByFacilityStates(facilityStates);
		return createFeatures(snapshots);
	}

	@Override
	public List<FacilityStateReportFeature> findByFacilityStatesUpdatedSince(Iterable<FacilityState> facilityStates,
			long timestamp) {
		final Iterable<FacilityStateSnapshot> snapshots = getFacilityStateSnapshotService()
				.findLastByFacilityStatesUpdatedSince(facilityStates, timestamp);
		return createFeatures(snapshots);
	}

	private List<FacilityStateReportFeature> createFeatures(final Iterable<FacilityStateSnapshot> snapshots) {
		final List<FacilityStateReportFeature> features = new LinkedList<>();
		for (final FacilityStateSnapshot snapshot : snapshots) {
			final FacilityStateReportFeature feature = createFeature(snapshot);
			features.add(feature);
		}
		return features;
	}

	private FacilityStateReportFeature createFeature(final FacilityStateSnapshot snapshot) {
		Facility facility = getFacilityService().findByEquipmentnumber(snapshot.getEquipmentnumber());
		if (facility == null) {
			facility = Facility.UNKNOWN;
		}
		Station station = getStationService().findByStationnumber(facility.getStationnumber());
		if (station == null) {
			station = Station.UNKNOWN;
		}
		final FacilityStateReportFeature feature = new FacilityStateReportFeature(station, facility, snapshot);
		return feature;
	}
}
