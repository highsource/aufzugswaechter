package org.hisrc.azw.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.AbstractSetValuedMap;
import org.apache.commons.lang3.Validate;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateSnapshot;
import org.hisrc.azw.service.FacilityStateSnapshotService;

public class MemoryBasedFacilityStateSnapshotService implements
		FacilityStateSnapshotService {

	private static final Comparator<FacilityStateSnapshot> FACILITY_STATE_SNAPSHOT_COMPARATOR = new Comparator<FacilityStateSnapshot>() {
		@Override
		public int compare(FacilityStateSnapshot o1, FacilityStateSnapshot o2) {
			Validate.notNull(o1);
			Validate.notNull(o2);
			return o2.getTimestamp().compareTo(o1.getTimestamp());
		}
	};

	private MultiValuedMap<Long, FacilityStateSnapshot> facilityStateSnapshots = new AbstractSetValuedMap<Long, FacilityStateSnapshot>(
			new HashMap<Long, TreeSet<FacilityStateSnapshot>>()) {
		@Override
		protected Set<FacilityStateSnapshot> createCollection() {
			return new TreeSet<FacilityStateSnapshot>(
					FACILITY_STATE_SNAPSHOT_COMPARATOR);
		}
	};

	@Override
	public FacilityStateSnapshot findLastByEquipmentnumber(long equipmentnumber) {
		final Iterator<FacilityStateSnapshot> iterator = this.facilityStateSnapshots
				.get(equipmentnumber).iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}

	@Override
	public void persistOrUpdate(FacilityStateSnapshot snapshot) {
		Validate.notNull(snapshot);
		this.facilityStateSnapshots
				.put(snapshot.getEquipmentnumber(), snapshot);
	}

	@Override
	public Iterable<FacilityStateSnapshot> findAllLast() {
		return findLastByFacilityStatesUpdatedSince(
				Arrays.asList(FacilityState.values()), Long.MIN_VALUE);
	}

	@Override
	public Iterable<FacilityStateSnapshot> findAllLastUpdatedSince(
			long timestamp) {
		return findLastByFacilityStatesUpdatedSince(
				Arrays.asList(FacilityState.values()), timestamp);
	}

	@Override
	public Iterable<FacilityStateSnapshot> findLastByFacilityStates(
			Iterable<FacilityState> facilityStates) {
		return findLastByFacilityStatesUpdatedSince(facilityStates,
				Long.MIN_VALUE);
	}

	@Override
	public Iterable<FacilityStateSnapshot> findLastByFacilityStatesUpdatedSince(
			Iterable<FacilityState> facilityStates, long timestamp) {
		final Set<FacilityState> states = new TreeSet<>(
				IterableUtils.toList(facilityStates));
		final List<FacilityStateSnapshot> snapshots = new LinkedList<>();
		synchronized (facilityStateSnapshots) {
			for (Entry<Long, Collection<FacilityStateSnapshot>> entry : facilityStateSnapshots
					.asMap().entrySet()) {
				final Iterator<FacilityStateSnapshot> iterator = entry
						.getValue().iterator();
				if (iterator.hasNext()) {
					final FacilityStateSnapshot snapshot = iterator.next();
					if (states.contains(snapshot.getState())
							&& snapshot.getTimestamp() > timestamp) {
						snapshots.add(snapshot);
					}
				}
			}
		}
		return snapshots;
	}
}
