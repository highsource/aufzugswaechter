package org.hisrc.azw.integration.impl.v_1_0;

import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateReport;
import org.hisrc.azw.model.FacilityType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class DefaultApiFacilityToFacilityStateReportConverter {

	private GeometryFactory geometryFactory = new GeometryFactory();

//	private double changeProbability = Double.MIN_VALUE;
//
//	public double getChangeProbability() {
//		return changeProbability;
//	}
//
//	public void setChangeProbability(double changeProbability) {
//		this.changeProbability = changeProbability;
//	}
//
	public FacilityStateReport asFacilityStateReport(org.hisrc.fasta.client.v1.model.Facility f) {

		final Facility facility = asFacility(f);
		final FacilityState facilityState;
//		final double changeProbability = getChangeProbability();
//		final boolean shouldNotBeFaked = Math.random() >= changeProbability;
		final boolean shouldNotBeFaked = true;
		switch (f.getState()) {
		case ACTIVE:
			facilityState = shouldNotBeFaked ? FacilityState.ACTIVE : FacilityState.TEST_ACTIVE;
			break;
		case INACTIVE:
			facilityState = shouldNotBeFaked ? FacilityState.INACTIVE : FacilityState.TEST_INACTIVE;
			break;
//		case UNKNOWN:
//			facilityState = shouldNotBeFaked ? FacilityState.UNKNOWN : FacilityState.TEST_UNKNOWN;
//			break;
		default:
			facilityState = null;
		}
		return new FacilityStateReport(facility, facilityState);
	}

	private Facility asFacility(org.hisrc.fasta.client.v1.model.Facility e) {
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
}
