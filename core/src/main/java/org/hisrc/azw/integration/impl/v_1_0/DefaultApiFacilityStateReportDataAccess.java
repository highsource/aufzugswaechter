package org.hisrc.azw.integration.impl.v_1_0;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.integration.FacilityStateReportDataAccess;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateReport;
import org.hisrc.azw.model.FacilityType;
import org.hisrc.dbeac.client.v_1_0.api.DefaultApi;
import org.hisrc.dbeac.client.v_1_0.invoker.ApiException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class DefaultApiFacilityStateReportDataAccess implements FacilityStateReportDataAccess {

	private GeometryFactory geometryFactory = new GeometryFactory();

	private DefaultApi api;

	@SuppressWarnings("unused")
	private DefaultApiFacilityStateReportDataAccess() {
	}

	public DefaultApiFacilityStateReportDataAccess(DefaultApi api) {
		Validate.notNull(api);
		this.api = api;
	}

	public DefaultApi getApi() {
		return api;
	}

	public void setApi(DefaultApi api) {
		this.api = api;
	}

	@Override
	public Iterable<FacilityStateReport> findAll() throws IOException {
		return findByFacilityTypesAndFacilityStates(null, null);
	}

	@Override
	public Iterable<FacilityStateReport> findByFacilityStates(Iterable<FacilityState> facilityStates)
			throws IOException {
		return findByFacilityTypesAndFacilityStates(null, facilityStates);
	}

	private Iterable<FacilityStateReport> findByFacilityTypesAndFacilityStates(Iterable<FacilityType> facilityTypes,
			Iterable<FacilityState> facilityStates) throws IOException {

		final List<String> types;
		if (facilityTypes == null) {
			types = null;
		} else {
			types = new LinkedList<String>();
			for (FacilityType facilityType : facilityTypes) {
				types.add(facilityType.name());
			}
		}
		final List<String> states;
		if (facilityStates == null) {
			states = null;
		} else {
			states = new LinkedList<String>();
			for (FacilityState facilityState : facilityStates) {
				states.add(facilityState.name());
			}
		}

		try {
			final List<org.hisrc.dbeac.client.v_1_0.model.Facility> fs = getApi().findFacilities(types, states);
			final List<FacilityStateReport> facilityStateReports = new ArrayList<>(fs.size());
			for (org.hisrc.dbeac.client.v_1_0.model.Facility f : fs) {
				facilityStateReports.add(asFacilityStateReport(f));
			}
			return facilityStateReports;
		} catch (ApiException cause) {
			throw new IOException("Error retrieving facilities.", cause);
		}
	}

	@Override
	public FacilityStateReport findByEquipmentnumber(long equipmentnumber) throws IOException {
		try {
			org.hisrc.dbeac.client.v_1_0.model.Facility f = getApi().getFacilityByEquipmentNumber(equipmentnumber);
			return asFacilityStateReport(f);
		} catch (ApiException cause) {
			// throw new IOException(
			// MessageFormat.format(
			// "Error retrieving facility with the equipment number [{0}].",
			// equipmentnumber), cause);
			return null;
		}
	}

	private Facility asFacility(org.hisrc.dbeac.client.v_1_0.model.Facility e) {
		final FacilityType facilityType;
		switch (e.getType()) {
		case ELEVATOR:
			facilityType = FacilityType.ELEVATOR;
			break;
		case ESCALATOR:
			facilityType = FacilityType.ESCALATOR;
			break;
		default:
			facilityType = null;
		}
		final Double x = e.getGeocoordX();
		final Double y = e.getGeocoordY();
		final Point geometry;
		if (x != null && y != null) {
			geometry = geometryFactory.createPoint(new Coordinate(x, y));
		} else {
			geometry = null;
		}
		return new Facility(e.getEquipmentnumber(), facilityType, e.getDescription(), geometry, e.getStationnumber());
	}

	private double changeProbability = 0.02;

	public double getChangeProbability() {
		return changeProbability;
	}

	public void setChangeProbability(double changeProbability) {
		this.changeProbability = changeProbability;
	}

	private FacilityStateReport asFacilityStateReport(org.hisrc.dbeac.client.v_1_0.model.Facility f) {

		final Facility facility = asFacility(f);
		final FacilityState facilityState;
		final boolean shouldNotBeFaked = Math.random() >= getChangeProbability();
		switch (f.getState()) {
		case ACTIVE:
			facilityState = shouldNotBeFaked ? FacilityState.ACTIVE : FacilityState.TEST_ACTIVE;
			break;
		case INACTIVE:
			facilityState = shouldNotBeFaked ? FacilityState.INACTIVE : FacilityState.TEST_INACTIVE;
			break;
		case UNKNOWN:
			facilityState = shouldNotBeFaked ? FacilityState.UNKNOWN : FacilityState.TEST_UNKNOWN;
			break;
		default:
			facilityState = null;
		}
		return new FacilityStateReport(facility, facilityState);
	}

}
