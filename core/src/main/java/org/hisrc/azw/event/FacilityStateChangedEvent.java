package org.hisrc.azw.event;

import java.text.MessageFormat;
import java.util.EventObject;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.model.Facility;
import org.hisrc.azw.model.FacilityStateSnapshot;
import org.hisrc.azw.model.Station;

public class FacilityStateChangedEvent extends EventObject {

	private static final long serialVersionUID = 2607203168473541899L;
	private final Facility facility;
	private final FacilityStateSnapshot oldSnapshot;
	private final FacilityStateSnapshot newSnapshot;

	public FacilityStateChangedEvent(Station station, Facility facility,
			FacilityStateSnapshot oldSnapshot, FacilityStateSnapshot newSnapshot) {
		super(Validate.notNull(station));
		Validate.notNull(facility);
		Validate.notNull(newSnapshot);
		this.facility = facility;
		this.oldSnapshot = oldSnapshot;
		this.newSnapshot = newSnapshot;
	}

	@Override
	public Station getSource() {
		return (Station) super.getSource();
	}

	public Facility getFacility() {
		return facility;
	}

	public FacilityStateSnapshot getOldSnapshot() {
		return oldSnapshot;
	}

	public FacilityStateSnapshot getNewSnapshot() {
		return newSnapshot;
	}

	@Override
	public String toString() {
		return MessageFormat
				.format("FacilityStateChangedEvent [station={0}, facility={1}, oldSnapshot={2}, newSnapshot={3}]",
						getSource(), getFacility(), getOldSnapshot(),
						getNewSnapshot());
	}

}
