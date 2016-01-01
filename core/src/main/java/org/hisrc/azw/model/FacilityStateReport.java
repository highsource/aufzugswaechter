package org.hisrc.azw.model;

import java.text.MessageFormat;

import org.apache.commons.lang3.Validate;

public class FacilityStateReport {

	private final Facility facility;
	private final FacilityState facilityState;

	public FacilityStateReport(Facility facility, FacilityState facilityState) {
		Validate.notNull(facility);
		Validate.notNull(facilityState);
		this.facility = facility;
		this.facilityState = facilityState;
	}

	public Facility getFacility() {
		return facility;
	}

	public FacilityState getFacilityState() {
		return facilityState;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((facility == null) ? 0 : facility.hashCode());
		result = prime * result
				+ ((facilityState == null) ? 0 : facilityState.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FacilityStateReport other = (FacilityStateReport) obj;
		if (facility == null) {
			if (other.facility != null)
				return false;
		} else if (!facility.equals(other.facility))
			return false;
		if (facilityState != other.facilityState)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return MessageFormat.format(
				"FacilityStateReport [facility={0}, facilityState={1}]",
				facility, facilityState);
	}

}
