package org.hisrc.azw.service.impl.tests;

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
		sut.persistOrUpdate(new FacilityStateSnapshot(0L, 0L, FacilityState.ACTIVE));
		sut.persistOrUpdate(new FacilityStateSnapshot(1L, 0L,
				FacilityState.INACTIVE));
		Assert.assertEquals("Wrong timestamp.", 1, sut
				.findLastByEquipmentnumber(0).getTimestamp().longValue());

	}

	@Test
	public void findsNullForUnknownEquipmentnumber() {
		Assert.assertNull(sut.findLastByEquipmentnumber(-1));
	}

}
