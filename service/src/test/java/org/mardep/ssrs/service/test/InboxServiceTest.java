package org.mardep.ssrs.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.domain.inbox.Task;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IInboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.AssertThrows;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class InboxServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IInboxService inbox;

	@BeforeClass
	public static void init() {
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
	}

	@Test
	public void testStart() throws Exception {
		Task[] tasks = doStart();
		for (Task t : tasks) {
			System.out.println(t.getId());
		}
	}

	private Task[] doStart() {
		String name = "newShipReg";
		String param1 = "1999/001";
		String param2 = "F";
		String param3 = "....";
		String properties = "{param1:\""
				+ param1
				+ "\", param2:\""
				+ param2
				+ "\", param3:\""
				+ param3
				+ "\"}";
		Task[] tasks = inbox.startWorkflow(name, param1, param2, param3, properties);
		return tasks;
	}

	@Test
	public void testProceed() {
		Task[] tasks = doStart();
		String properties = "{}";
		for (Task t : tasks) {
			System.out.println(t.getName());
			try {
				Task[] followUp = inbox.proceed(t.getId(), "next", properties);
				assertNotSame(t.getName(), "wait");
				for (Task f : followUp) {
					System.out.println(f.getName());
				}
			} catch (IllegalArgumentException e) {
				assertEquals(t.getName(), "wait");
			}
		}
	}

	@Test
	public void testNewShipReg() {
		Task[] tasks = inbox.startWorkflow("newShipRegWorkflow", "1999/001", "", "", "{}");

		assertEquals(1, tasks.length);
		assertEquals("newShipReg.received", tasks[0].getName());

		Task[] postRequestAccept = inbox.proceed(tasks[0].getId(), "requestAccept", "{}");

		assertEquals("newShipReg.pending.Accept", postRequestAccept[0].getName());
		assertEquals(tasks[0].getId(), postRequestAccept[0].getParentId());
		assertEquals(null, postRequestAccept[0].getActionBy());
		assertEquals(null, postRequestAccept[0].getActionDate());
		assertEquals(null, postRequestAccept[0].getActionPerformed());

		Task[] postReject = inbox.proceed(postRequestAccept[0].getId(), "reject", "{}");
		assertEquals(0, postReject.length);

		Task pendingAccept = inbox.getTask(postRequestAccept[0].getId());
		assertEquals("Test", pendingAccept.getActionBy());
		assertEquals(Boolean.TRUE, pendingAccept.getActionDate().getTime() < System.currentTimeMillis());
		assertEquals("reject", pendingAccept.getActionPerformed());
	}

}
