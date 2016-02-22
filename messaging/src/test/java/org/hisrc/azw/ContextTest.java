package org.hisrc.azw;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
		//
		"/org/hisrc/azw/camel/config.xml",
		//
		"/org/hisrc/azw/email/config.xml",
		//
		"/org/hisrc/azw/recaptcha/config.xml" })
public class ContextTest {

	// @Injec

	@Test
	public void test() {

	}

}
