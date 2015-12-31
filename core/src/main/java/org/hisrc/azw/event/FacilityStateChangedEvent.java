package org.hisrc.azw.event;

import java.text.MessageFormat;
import java.util.EventObject;

import org.apache.commons.lang3.Validate;
import org.hisrc.dbeac.client.v_1_0.model.Facility;
import org.hisrc.dbeac.client.v_1_0.model.Station;

public class FacilityStateChangedEvent extends EventObject {

	private static final long serialVersionUID = 2607203168473541899L;
	private final Facility oldValue;
	private final Facility newValue;

	public FacilityStateChangedEvent(Station station, Facility oldValue,
			Facility newValue) {
		super(Validate.notNull(station));
		Validate.notNull(newValue);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public Station getSource() {
		return (Station) super.getSource();
	}

	public Facility getOldValue() {
		return oldValue;
	}

	public Facility getNewValue() {
		return newValue;
	}

	@Override
	public String toString() {
		return MessageFormat
				.format("FacilityStateChangedEvent [station ={0}, oldValue={1}, newValue={2}]",
						getSource(), oldValue, newValue);
	}
}
