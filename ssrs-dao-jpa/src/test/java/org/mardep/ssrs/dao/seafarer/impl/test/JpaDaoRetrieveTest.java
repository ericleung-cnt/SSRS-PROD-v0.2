package org.mardep.ssrs.dao.seafarer.impl.test;

import java.util.Date;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.OwnerPK;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:jpaDao-test.xml")
public class JpaDaoRetrieveTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ApplicationContext context;
	
	@SuppressWarnings("rawtypes")
	@Test
    @Transactional
    @Rollback(true)
    public void testFind(){
        logger.info("#test find..................");
        int num = 0;
        Map<String, IBaseDao> map = context.getBeansOfType(IBaseDao.class);
        for(IBaseDao baseDao : map.values()){
        	baseDao.findAll();
        	num++;
        }
        logger.info("Total retrieve Test:{}", num);
	}
	
	@Before
	public void init() {
		User user = new User();
		user.setId("TESTING");
		UserContextThreadLocalHolder.setCurrentUser(user);
	}

	@Autowired
	IOwnerDao ownerDao;
	
	@Autowired
	IRegMasterDao rmDao;
	
	@Test
    @Transactional
    @Rollback(false)
	public void testOwner() {
		
		
		OwnerPK id = new OwnerPK("1999/001", 3);
		Owner owner = ownerDao.findById(id);
		if (owner != null) {
			owner.setName("xxd3");
			Owner newOwner = ownerDao.save(owner);
			
			System.out.println(newOwner.getName());
		}
	}
	 
}
