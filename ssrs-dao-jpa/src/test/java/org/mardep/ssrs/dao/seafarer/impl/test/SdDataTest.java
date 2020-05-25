package org.mardep.ssrs.dao.seafarer.impl.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.sr.ISdDataDao;
import org.mardep.ssrs.domain.sr.SdData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:jpaDao-test.xml")
public class SdDataTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired private ISdDataDao sdDataDao;
	
	@Test
	public void testFind() {
		List<SdData> list = sdDataDao.findAll();
		System.out.println(list.size());
	}
}
