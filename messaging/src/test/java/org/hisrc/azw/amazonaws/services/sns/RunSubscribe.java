package org.hisrc.azw.amazonaws.services.sns;

import javax.inject.Inject;

import org.hisrc.azw.email.FacilityStateChangedEventEmailSubscriptionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/org/hisrc/azw/email/config.xml")
public class RunSubscribe {

	@Inject
	private FacilityStateChangedEventEmailSubscriptionService emailSubscriptionService;

	@Test
	public void test() {
		emailSubscriptionService.subscribeForAllFacilities("me");
	}

}
