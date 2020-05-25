package org.mardep.ssrs.dao.seafarer.impl.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dao.sr.IReservedShipNameDao;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.ReservedShipName;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:jpaDao-test.xml")
public class SrTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired private IReservedShipNameDao nameDao;
	@Autowired private IRegMasterDao rmDao;
	
	@Test
    public void testFind(){
        logger.info("#test find..................");
        
        ReservedShipName entity = new ReservedShipName();
        entity.setExpiryDate(new Date());
		List<ReservedShipName> names = nameDao.findByCriteria(entity);
		RegMaster reg = new RegMaster();
		reg.setRegChiName("regChiName");
		reg.setRegName("regName");
		List<RegMaster> regs = rmDao.findByCriteria(reg);
		System.out.println(names);
    }
	
	@Test
	@Transactional
	@Rollback(false)
	public void testCreate() {
        User user = new User();
        user.setId(this.getClass().getSimpleName());
		UserContextThreadLocalHolder.setCurrentUser(user);
		
		String reserving = "x";
		String applNo;
		
		int thisYear = Calendar.getInstance().get(Calendar.YEAR);
		String max = rmDao.maxApplNo(thisYear);
		if (max == null) {
			applNo = thisYear + "/001";
		} else {
			if (max.length() > 5) {
				int next = Integer.parseInt(max.substring(5)) + 1;
				if (next < 1000) {
					applNo = thisYear + "/" + String.valueOf(1000 + next).substring(1);
				} else {
					applNo = thisYear + "/" + String.valueOf(next);
				}
			} else {
				applNo = thisYear + "/001";
			}
		}		

		RegMaster regMaster = new RegMaster();
		regMaster.setApplNo(applNo);
		List<RegMaster> regs = rmDao.findByCriteria(regMaster);
		if (regs.isEmpty()) {
			rmDao.save(regMaster);
		}
		
		ReservedShipName name = new ReservedShipName();
		name.setApplNo(applNo);
		name.setName(reserving);
		if (name.getReleaseTime() != null) {
			if (name.getReleaseReason() == null) { 
				throw new IllegalArgumentException("release must be come with reason");
			}
		}
		nameDao.save(name);
	}
	
}
