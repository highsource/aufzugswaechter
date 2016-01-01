package org.hisrc.azw.model;

import java.io.Serializable;
import java.text.MessageFormat;

import org.apache.commons.lang3.Validate;

public class FacilityStateSnapshot implements Serializable {

	private static final long serialVersionUID = -8056240834976598640L;
	private Long timestamp;
	private Long equipmentnumber;
	private FacilityState state;

	public FacilityStateSnapshot(Long timestamp, Long equipmentnumber,
			FacilityState state) {
		Validate.notNull(timestamp);
		Validate.notNull(equipmentnumber);
		Validate.notNull(state);
		this.timestamp = timestamp;
		this.equipmentnumber = equipmentnumber;
		this.state = state;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public Long getEquipmentnumber() {
		return equipmentnumber;
	}

	public FacilityState getState() {
		return state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((equipmentnumber == null) ? 0 : equipmentnumber.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
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
		FacilityStateSnapshot other = (FacilityStateSnapshot) obj;
		if (equipmentnumber == null) {
			if (other.equipmentnumber != null)
				return false;
		} else if (!equipmentnumber.equals(other.equipmentnumber))
			return false;
		if (state != other.state)
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return MessageFormat
				.format("FacilityStateEvent [timestamp={0}, equipmentnumber={1}, state={2}]",
						timestamp, equipmentnumber, state);
	}

}
