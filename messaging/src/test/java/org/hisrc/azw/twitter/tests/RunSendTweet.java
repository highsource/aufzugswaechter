package org.hisrc.azw.twitter.tests;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Route;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/org/hisrc/azw/twitter/config.xml")
public class RunSendTweet {

	@Inject
	private ProducerTemplate template;

	@Test
	public void test() {
//		Route route = context.getRoutes().get(0);
//		final ProducerTemplate template = context.createProducerTemplate();
		template.sendBody("direct:laconic", "Test tweet");
	}

}
