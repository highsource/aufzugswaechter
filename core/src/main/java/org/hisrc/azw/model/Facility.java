package org.hisrc.azw.model;

import java.io.Serializable;
import java.text.MessageFormat;

import org.apache.commons.lang3.Validate;

import com.vividsolutions.jts.geom.Point;

public class Facility implements Serializable {

	public static Facility UNKNOWN = new Facility(-1L, FacilityType.ELEVATOR,
			"<unknown facility>", null, Station.UNKNOWN.getStationnumber());

	private static final long serialVersionUID = -4854190538794980599L;
	private Long equipmentnumber;
	private FacilityType type;
	private String description;
	private Point geometry;
	private Long stationnumber;

	public Facility(Long equipmentnumber, FacilityType type,
			String description, Point geometry, Long stationnumber) {
		Validate.notNull(equipmentnumber);
		Validate.notNull(type);
		Validate.notNull(stationnumber);
		this.equipmentnumber = equipmentnumber;
		this.type = type;
		this.description = description;
		this.geometry = geometry;
		this.stationnumber = stationnumber;
	}

	public Long getEquipmentnumber() {
		return equipmentnumber;
	}

	public FacilityType getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public Point getGeometry() {
		return geometry;
	}

	public Long getStationnumber() {
		return stationnumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((equipmentnumber == null) ? 0 : equipmentnumber.hashCode());
		result = prime * result
				+ ((geometry == null) ? 0 : geometry.hashCode());
		result = prime * result
				+ ((stationnumber == null) ? 0 : stationnumber.hashCode());
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
		Facility other = (Facility) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (equipmentnumber == null) {
			if (other.equipmentnumber != null)
				return false;
		} else if (!equipmentnumber.equals(other.equipmentnumber))
			return false;
		if (geometry == null) {
			if (other.geometry != null)
				return false;
		} else if (!geometry.equals(other.geometry))
			return false;
		if (stationnumber == null) {
			if (other.stationnumber != null)
				return false;
		} else if (!stationnumber.equals(other.stationnumber))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return MessageFormat
				.format("Facility [equipmentnumber={0}, type={1}, description={2}, geometry={3}, stationnumber={4}]",
						equipmentnumber, type, description, geometry,
						stationnumber);
	}

}
