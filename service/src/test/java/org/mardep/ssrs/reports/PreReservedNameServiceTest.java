package org.mardep.ssrs.reports;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.inbox.ITaskDao;
import org.mardep.ssrs.dao.sr.IPreReservedNameDao;
import org.mardep.ssrs.domain.inbox.Task;
import org.mardep.ssrs.domain.sr.PreReserveApp;
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
	
	@Autowired ITaskDao taskDao;

	@Autowired IPreReservedNameDao nameDao;
	
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
		name.setName("testn " + suffix);
		name.setExpiryTime(new Date(System.currentTimeMillis() + 3L * 365* 24 * 3600));
		return rs.savePreReservation(name);
	}

	@Test public void testCheck() {
		PreReservedName created = create();

		Map<String, String[]> resultEn = rs.check(Arrays.asList(new String[] {created.getName()}), true);
		assertEquals(0, resultEn.size());

		Map<String, String[]> resultCh = rs.check(Arrays.asList(new String[] {created.getName()}), false);
		assertEquals(0, resultCh.size());

		Map<String, String[]> resultEmpty = rs.check(Arrays.asList(new String[] {created.getName()}), true);
		assertEquals(0, resultEmpty.size());
	}
	
	@Test
	@Transactional
	public void testReserve() {
		PreReserveApp entity = new PreReserveApp();
		entity.setName1("unit test " + System.currentTimeMillis());
		entity.setChName1("unit test chi " + System.currentTimeMillis());
		entity.setName(entity.getName1());
		entity.setChName(entity.getChName1());
		entity.setOwner("onwer 1");
		entity.setApplicant("appl 1");
		PreReserveApp savePreReserveApp = rs.savePreReserveApp(entity);
		String name = "preReserveApp_received";
		rs.reserve(savePreReserveApp.getId(), savePreReserveApp.getName(), 
				savePreReserveApp.getChName(), 
				findTask(savePreReserveApp, name));
		
	}

	@Test
	@Transactional
	public void testRelease() {
		PreReserveApp entity = new PreReserveApp();
		entity.setName1("unit test " + System.currentTimeMillis());
		entity.setChName1("unit test chi " + System.currentTimeMillis());
		entity.setName(entity.getName1());
		entity.setChName(entity.getChName1());
		entity.setOwner("onwer 1");
		entity.setApplicant("appl 1");
		PreReserveApp savePreReserveApp = rs.savePreReserveApp(entity);
		String name = "preReserveApp_received";
		rs.release(savePreReserveApp.getId(), "UNIT TEST", 
				findTask(savePreReserveApp, name));
	}

	private Long findTask(PreReserveApp savePreReserveApp, String name) {
		Task task = new Task();
		task.setName(name);
		task.setParam1(""+savePreReserveApp.getId());
		Long taskId = taskDao.findByCriteria(task).get(0).getId();
		return taskId;
	}

}
