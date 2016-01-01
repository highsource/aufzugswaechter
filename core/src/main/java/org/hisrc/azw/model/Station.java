package org.hisrc.azw.model;

import java.io.Serializable;
import java.text.MessageFormat;

import org.apache.commons.lang3.Validate;

public class Station implements Serializable {

	public static final Station UNKNOWN = new Station(-1L, "<unknown station>");

	private static final long serialVersionUID = 643919943074176324L;
	private Long stationnumber = null;
	private String name = null;

	public Station(Long stationnumber, String name) {
		Validate.notNull(stationnumber);
		Validate.notNull(name);
		this.stationnumber = stationnumber;
		this.name = name;
	}

	public Long getStationnumber() {
		return stationnumber;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return MessageFormat.format("Station [stationnumber={0}, name={1}]",
				stationnumber, name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((stationnumber == null) ? 0 : stationnumber.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		final Station other = (Station) obj;
		if (stationnumber == null) {
			if (other.stationnumber != null) {
				return false;
			}
		} else if (!stationnumber.equals(other.stationnumber)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
