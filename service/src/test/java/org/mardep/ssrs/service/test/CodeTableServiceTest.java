package org.mardep.ssrs.service.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.domain.codetable.Country;
import org.mardep.ssrs.domain.codetable.Nationality;
import org.mardep.ssrs.domain.codetable.ShipList;
import org.mardep.ssrs.domain.codetable.ShipSubType;
import org.mardep.ssrs.domain.codetable.ShipType;
import org.mardep.ssrs.domain.codetable.StopList;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.ICodeTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class CodeTableServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ICodeTableService codeTableService; 
	
	@Test
	public void testCreateNationalityTest() throws Exception {
		logger.info("### test create Nationality");
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		for(int i=0;i<100;i++){
			Nationality entity = new Nationality();
			entity.setEngDesc("Name "+i);
			entity.setCountryEngDesc("Name "+i);
			entity.setChiDesc("名 "+i);
			codeTableService.save(entity);
		}
		TimeUnit.SECONDS.sleep(1);
	}

	@Test
	public void testCreateCountryTest() throws Exception {
		logger.info("### test create Country");
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		for(int i=0;i<2100;i++){
			Country entity = new Country();
			entity.setId("Id:"+i);
			entity.setName("国家名称："+i);
			codeTableService.save(entity);
		}
		TimeUnit.SECONDS.sleep(1);
	}

	@Test
	public void testCreateShipTypeTest() throws Exception {
		logger.info("### test create ShipType");
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		for(int i=0;i<5;i++){
			ShipType entity = new ShipType();
			entity.setId("S"+i);
			entity.setStDesc("Desc...");
			codeTableService.save(entity);
		}
		TimeUnit.SECONDS.sleep(1);
	}

	@Test
	public void testCreateShipSubTypeTest() throws Exception {
		logger.info("### test create ShipType");
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		for(int i=0;i<5;i++){
			ShipSubType entity = new ShipSubType();
			entity.setShipSubTypeCode("Sub"+i);
			entity.setShipTypeCode("S"+i);
			entity.setSsDesc("DESC..");
			codeTableService.save(entity);
		}
		TimeUnit.SECONDS.sleep(1);
	}

	@Test
	public void testCreateShipListTest() throws Exception {
		logger.info("### test create Ship List");
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		for(int i=0;i<9999;i++){
			ShipList entity = new ShipList();
			entity.setVesselName("Vessel Name:"+i);
			entity.setCompanyName("Company :"+i);
			entity.setFlag("F:"+i);
			codeTableService.save(entity);
		}
		TimeUnit.SECONDS.sleep(1);
	}
	
}
