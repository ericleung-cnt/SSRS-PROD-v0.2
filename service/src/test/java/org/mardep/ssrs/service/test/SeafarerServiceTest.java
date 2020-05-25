package org.mardep.ssrs.service.test;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.seafarer.ISeafarerDao;
import org.mardep.ssrs.domain.constant.EyeTestResult;
import org.mardep.ssrs.domain.seafarer.EyeTest;
import org.mardep.ssrs.domain.seafarer.Medical;
import org.mardep.ssrs.domain.seafarer.Reg;
import org.mardep.ssrs.domain.seafarer.Seafarer;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.ISeafarerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class SeafarerServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ISeafarerService seafarerService; 

	@Autowired
	ISeafarerDao seafarerDao; 
	
	@Test
	public void testCreateEyeTest() throws Exception {
		logger.info("### testCreateEyeTest");
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		
		EyeTest entity = new EyeTest();
		entity.setColourTest(EyeTestResult.PASS);
		entity.setLetterTest(EyeTestResult.FAIL);
		entity.setEyeSightTest(EyeTestResult.NIL);
		entity.setSeafarerId("0001");
		seafarerService.save(entity);
		TimeUnit.SECONDS.sleep(1);
	}
	
	@Test
	public void testCreateMedical() throws Exception {
		logger.info("### testCreateEyeTest");
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		
		Medical entity = new Medical();
		entity.setSeqNo(1); 
		entity.setSeafarerId("0001");
		entity.setExpiryDate(new Date());
		entity.setExamDate(new Date());
		entity.setExamPlace("Exam Place");
		seafarerService.save(entity);
		TimeUnit.SECONDS.sleep(1);
	}
	
	@Test
	public void testUpdateMedical() throws Exception {
		logger.info("### test update Medical ");
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		Medical entity = new Medical();
		
		entity.setSeqNo(1); 
		entity.setSeafarerId("0001");
		entity.setExpiryDate(new Date());
		entity.setExamDate(new Date());
		entity.setExamPlace(""+new Date().toString());
		entity.setCreatedBy("Test");
		entity.setCreatedDate(new Date());
		entity.setVersion(4L);
		seafarerService.save(entity);
		TimeUnit.SECONDS.sleep(1);
	}

	@Test
	public void testSearchSeafarer() throws Exception {
		seafarerService.findByCriteria(new Seafarer());
	}
	
	@Test
	public void testCreateSeafarer() throws Exception {
		logger.info("### test create Seafarer ");
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		for(int i=3;i<500;i++){
			Seafarer entity = new Seafarer();
			entity.setId("Seafarer "+i);
			if(i%2==0){
				entity.setPartType("1");
			}else{
				entity.setPartType("2");
			}
			entity.setSurname("CHEN"+i);
			entity.setFirstName("DA WEN "+i);
			entity.setSerbNo("SERB NO");
			entity.setSeqNo(1);
			seafarerService.save(entity);
		}
		TimeUnit.SECONDS.sleep(1);
	}

	@Test
	public void testReIssueSerb() throws Exception{
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		final String serbNo ="O1234567";
		final String seafarerId = "E542705(7)";
		Seafarer s = seafarerDao.findById(seafarerId);
		s.setSerbNo(serbNo);
		seafarerService.reIssueSerb(s, "test", new Date());
	}
	
	@Test
	public void testRenewSeafarerReg() throws Exception{
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		final String seafarerId = "G583997(0)";
		Reg newReg = new Reg();
		newReg.setSeafarerId(seafarerId);
		newReg.setRegDate(new Date());
		newReg.setRegExpiry(DateUtils.setYears(new Date(), 2021));
		newReg.setRegCancel(DateUtils.setMonths(DateUtils.setYears(new Date(), 2020), 10));
		seafarerService.renew(newReg);
	}
	
}
