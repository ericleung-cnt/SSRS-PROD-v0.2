package org.mardep.ssrs.service.test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.domain.inbox.Task;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IDeRegService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class DeRegServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IDeRegService deReg;

	@BeforeClass
	public static void init() {
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
	}

	@Test
	public void testStart() throws Exception {
		Task[] received = deReg.receive("1999/001", true);
		System.err.println(received[0].getId());
		System.err.println(received[0].getName());
		System.err.println(received[0].getCreatedBy());
		System.err.println(received[0].getCreatedDate());
		System.err.println(received[0].getActionBy());
		System.err.println(received[0].getActionDate());
	}

}
