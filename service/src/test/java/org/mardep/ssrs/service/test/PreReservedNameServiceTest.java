package org.mardep.ssrs.service.test;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.domain.inbox.Task;
import org.mardep.ssrs.domain.sr.PreReservedName;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IInboxService;
import org.mardep.ssrs.service.IReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class PreReservedNameServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IReservationService rs;

	@Autowired IInboxService inbox;

	@BeforeClass
	public static void init() {
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
	}

	@Test
	public void testCreate() {
		create();
	}

	private PreReservedName create() {
		PreReservedName name = new PreReservedName();
		Date entryTime = new Date();
		String suffix = new SimpleDateFormat("MMddHHmmss").format(entryTime);
		String address1 = "add1 " + suffix;
		String address2= "ad2 " + suffix;
		String address3= "ad3 " + suffix;
		String applName= "an " + suffix;
		String faxNo = "fn " + suffix;
		String ownerName = "on " + suffix;;
		String telNo = "t2 " + suffix;
		name.setAddress1(address1);
		name.setAddress2(address2);
		name.setAddress3(address3);
		name.setApplName(applName);
		name.setFaxNo(faxNo);
		name.setOwnerName(ownerName);
		name.setTelNo(telNo);
		return rs.savePreReservation(name);
	}

	@Test public void testCheck() {
		PreReservedName created = create();

		Map<String, String[]> resultEn = rs.check(Arrays.asList(new String[] {created.getName()}), true);
		assertEquals(1, resultEn.size());
		System.out.println("result En " + resultEn.keySet() + Arrays.asList(resultEn.values().iterator().next()));

		Map<String, String[]> resultCh = rs.check(Arrays.asList(new String[] {created.getName()}), false);
		assertEquals(1, resultCh.size());
		System.out.println("result Ch " + resultCh.keySet() + Arrays.asList(resultCh.values().iterator().next()));

		Map<String, String[]> resultEmpty = rs.check(Arrays.asList(new String[] {created.getName()}), true);
		assertEquals(0, resultEmpty.size());
	}

}
