package org.mardep.ssrs.dns;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * this is simulate SSRS DNS Inbound Service
 * 
 *
 */
@ContextConfiguration(value = { "classpath:/server/dns/http-server.xml", "classpath:/server/dns/dns-sim-inbound.xml", "classpath:/server/dns/dns-sim-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class DnsSimulatorInTest extends AbstractJUnit4SpringContextTests {

	private final static Logger logger = LoggerFactory.getLogger(DnsSimulatorInTest.class);
	
	@Test
	public void testDnsInbound() throws InterruptedException {
		logger.info("####################");
		logger.info("Start up DNS Simulator");
		logger.info("####################");
		TimeUnit.MINUTES.sleep(100);
	}
}