package org.hisrc.azw.service.impl.tests;

import java.util.Arrays;

import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateSnapshot;
import org.hisrc.azw.service.FacilityStateSnapshotService;
import org.hisrc.azw.service.impl.MemoryBasedFacilityStateSnapshotService;
import org.junit.Assert;
import org.junit.Test;

public class MemoryBasedFacilityStateSnapshotServiceTest {

	private FacilityStateSnapshotService sut = new MemoryBasedFacilityStateSnapshotService();

	@Test
	public void findsLastByEquipmentnumber() {
		final FacilityStateSnapshot a = new FacilityStateSnapshot(0L,
				FacilityState.ACTIVE, 0L);
		final FacilityStateSnapshot b = new FacilityStateSnapshot(0L,
				FacilityState.INACTIVE, 2L);
		sut.persistOrUpdate(a);
		sut.persistOrUpdate(b);
		Assert.assertEquals("Wrong snapshot.", b,
				sut.findLastByEquipmentnumber(0));
		Assert.assertEquals(Arrays.asList(b), sut.findAllLast());
		Assert.assertEquals(Arrays.asList(b), sut.findAllLastUpdatedSince(1L));
		Assert.assertEquals(Arrays.asList(), sut
				.findLastByFacilityStates(Arrays.asList(FacilityState.ACTIVE)));
		Assert.assertEquals(Arrays.asList(b),
				sut.findLastByFacilityStates(Arrays
						.asList(FacilityState.INACTIVE)));
		Assert.assertEquals(Arrays.asList(b), sut
				.findLastByFacilityStates(Arrays.asList(FacilityState.ACTIVE,
						FacilityState.INACTIVE)));
		Assert.assertEquals(
				Arrays.asList(b),
				sut.findLastByFacilityStatesUpdatedSince(
						Arrays.asList(FacilityState.INACTIVE), 1L));

	}

	@Test
	public void findsNullForUnknownEquipmentnumber() {
		Assert.assertNull(sut.findLastByEquipmentnumber(-1));
	}

}
