package org.hisrc.azw.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Arrays;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateSnapshot;
import org.hisrc.azw.model.FacilityType;
import org.hisrc.azw.model.Station;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class FacilityStateReportFeature implements Serializable {

	private static final long serialVersionUID = -3043540888435358225L;
	private final String type = "feature";
	private final FeatureProperties properties;
	private final FeatureGeometry geometry;

	public FacilityStateReportFeature(Station station,
			Facility facility, FacilityStateSnapshot facilityStateSnapshot) {
		this(Validate.notNull(station).getStationnumber(), Validate.notNull(
				station).getName(), Validate.notNull(facility)
				.getEquipmentnumber(), Validate.notNull(facility).getType(),
				Validate.notNull(facility).getDescription(), Validate.notNull(
						facility).getGeometry(), Validate.notNull(
						facilityStateSnapshot).getState(), Validate.notNull(
						facilityStateSnapshot).getTimestamp());
	}

	public FacilityStateReportFeature(Long stationnumber,
			String stationname, Long facilityEquipmentnumber,
			FacilityType facilityType, String facilityDescription,
			Point facilityGeometry, FacilityState facilityState,
			Long facilityStateKnownSince) {

		this.properties = new FeatureProperties(stationnumber, stationname,
				facilityEquipmentnumber, facilityType, facilityDescription,
				facilityState, facilityStateKnownSince);
		if (facilityGeometry != null) {
			this.geometry = new FeatureGeometry(facilityGeometry);
		} else {
			this.geometry = null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((geometry == null) ? 0 : geometry.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		FacilityStateReportFeature other = (FacilityStateReportFeature) obj;
		if (geometry == null) {
			if (other.geometry != null)
				return false;
		} else if (!geometry.equals(other.geometry))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return MessageFormat
				.format("FacilityStateReportFeature [type={0}, properties={1}, geometry={2}]",
						type, properties, geometry);
	}

	public String getType() {
		return type;
	}

	public FeatureProperties getProperties() {
		return properties;
	}

	public FeatureGeometry getGeometry() {
		return geometry;
	}

	public static class FeatureProperties implements Serializable {

		private static final long serialVersionUID = 5277343593126949663L;
		private final Long stationnumber;
		private final String stationname;
		private final Long facilityEquipmentnumber;
		private final FacilityType facilityType;
		private final String facilityDescription;
		private final FacilityState facilityState;
		private final Long facilityStateKnownSince;

		public FeatureProperties(Long stationnumber, String stationname,
				Long facilityEquipmentnumber, FacilityType facilityType,
				String facilityDescription, FacilityState facilityState,
				Long facilityStateKnownSince) {
			Validate.notNull(stationnumber);
			Validate.notNull(stationname);
			Validate.notNull(facilityEquipmentnumber);
			Validate.notNull(facilityType);
			this.stationnumber = stationnumber;
			this.stationname = stationname;
			this.facilityEquipmentnumber = facilityEquipmentnumber;
			this.facilityType = facilityType;
			this.facilityDescription = facilityDescription;
			this.facilityState = facilityState;
			this.facilityStateKnownSince = facilityStateKnownSince;
		}

		public Long getStationnumber() {
			return stationnumber;
		}

		public String getStationname() {
			return stationname;
		}

		public Long getFacilityEquipmentnumber() {
			return facilityEquipmentnumber;
		}

		public FacilityType getFacilityType() {
			return facilityType;
		}

		public String getFacilityDescription() {
			return facilityDescription;
		}

		public FacilityState getFacilityState() {
			return facilityState;
		}

		public Long getFacilityStateKnownSince() {
			return facilityStateKnownSince;
		}

		@Override
		public String toString() {
			return MessageFormat
					.format("Properties [stationnumber={0}, stationname={1}, facilityEquipmentnumber={2}, facilityType={3}, facilityDescription={4}, facilityState={5}, facilityStateKnownSince={6}]",
							stationnumber, stationname,
							facilityEquipmentnumber, facilityType,
							facilityDescription, facilityState,
							facilityStateKnownSince);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((facilityDescription == null) ? 0 : facilityDescription
							.hashCode());
			result = prime
					* result
					+ ((facilityEquipmentnumber == null) ? 0
							: facilityEquipmentnumber.hashCode());
			result = prime * result
					+ ((facilityState == null) ? 0 : facilityState.hashCode());
			result = prime
					* result
					+ ((facilityStateKnownSince == null) ? 0
							: facilityStateKnownSince.hashCode());
			result = prime * result
					+ ((facilityType == null) ? 0 : facilityType.hashCode());
			result = prime * result
					+ ((stationname == null) ? 0 : stationname.hashCode());
			result = prime * result
					+ ((stationnumber == null) ? 0 : stationnumber.hashCode());
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
			FeatureProperties other = (FeatureProperties) obj;
			if (facilityDescription == null) {
				if (other.facilityDescription != null)
					return false;
			} else if (!facilityDescription.equals(other.facilityDescription))
				return false;
			if (facilityEquipmentnumber == null) {
				if (other.facilityEquipmentnumber != null)
					return false;
			} else if (!facilityEquipmentnumber
					.equals(other.facilityEquipmentnumber))
				return false;
			if (facilityState != other.facilityState)
				return false;
			if (facilityStateKnownSince == null) {
				if (other.facilityStateKnownSince != null)
					return false;
			} else if (!facilityStateKnownSince
					.equals(other.facilityStateKnownSince))
				return false;
			if (facilityType != other.facilityType)
				return false;
			if (stationname == null) {
				if (other.stationname != null)
					return false;
			} else if (!stationname.equals(other.stationname))
				return false;
			if (stationnumber == null) {
				if (other.stationnumber != null)
					return false;
			} else if (!stationnumber.equals(other.stationnumber))
				return false;
			return true;
		}

	}

	public static class FeatureGeometry implements Serializable {
		private static final long serialVersionUID = 3367177686922031632L;
		private final String type = "Point";
		private final BigDecimal[] coordinates;

		public FeatureGeometry(Point geometry) {
			Validate.notNull(geometry);
			final Coordinate coordinate = geometry.getCoordinate();
			Validate.notNull(coordinate);
			Validate.isTrue(coordinate.x != Coordinate.NULL_ORDINATE);
			Validate.isTrue(coordinate.y != Coordinate.NULL_ORDINATE);
			this.coordinates = new BigDecimal[] { new BigDecimal(coordinate.x),
					new BigDecimal(coordinate.y) };
		}

		public String getType() {
			return type;
		}

		public BigDecimal[] getCoordinates() {
			return new BigDecimal[] { coordinates[0], coordinates[1] };
		}

		@Override
		public String toString() {
			return MessageFormat.format("Geometry [type={0}, coordinates={1}]",
					type, Arrays.toString(coordinates));
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(coordinates);
			result = prime * result + ((type == null) ? 0 : type.hashCode());
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
			FeatureGeometry other = (FeatureGeometry) obj;
			if (!Arrays.equals(coordinates, other.coordinates))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}

	}
}
