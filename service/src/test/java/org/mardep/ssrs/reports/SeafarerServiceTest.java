package org.mardep.ssrs.reports;

import java.math.BigDecimal;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.codetable.ICrewListCoverDao;
import org.mardep.ssrs.dao.seafarer.IPreviousSerbDao;
import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.domain.codetable.CrewListCover;
import org.mardep.ssrs.domain.seafarer.Cert;
import org.mardep.ssrs.domain.seafarer.PreviousSerb;
import org.mardep.ssrs.domain.seafarer.Reg;
import org.mardep.ssrs.domain.seafarer.Seafarer;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.ISeafarerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class SeafarerServiceTest {
	@Autowired
	ISeafarerService ss;
	@Autowired
	ICrewListCoverDao ccDao;
	
	@Autowired
	IPreviousSerbDao previousSerbDao;
	@Before
	public void init() {
		User user = new User();
		user.setId("TESTER");
		UserContextThreadLocalHolder.setCurrentUser(user);
	}
	@Transactional
	@Test
	@Rollback(false)
	public void testService() throws AddressException, MessagingException {
		String coverYymm = "201904";
		String vesselId = "UNITTEST " + System.currentTimeMillis() ;
		CrewListCover cover = new CrewListCover();
		cover.setVesselId(vesselId);
		cover.setCoverYymm(coverYymm);
		ccDao.save(cover);
		
		Crew crew = new Crew();
		crew.setCoverYymm(coverYymm);
		crew.setReferenceNo(1);
		crew.setVesselId(cover.getVesselId());
		crew.setBirthDate(new Date());
		crew.setCapacityId(1L);
		crew.setDischargeDate(new Date());
		crew.setEngageDate(new Date());
		crew.setNationalityId(1L);
		crew.setSalary(BigDecimal.valueOf(12345.54));
		ss.add(crew);
		
		Seafarer sf = new Seafarer();
		sf.setId(""+System.currentTimeMillis());
		sf.setFirstName("SfirstName");
		sf.setSurname("sfsurname");
		sf.setPartType("1");
		sf.setSeqNo((int) System.currentTimeMillis());
		sf = ss.save(sf);
		Cert cert = new Cert();
		cert.setSeafarerId(sf.getId());
		cert.setSeqNo(1);
		cert.setCertNo("1");
		cert.setCertType("xx");
		cert.setIssueAuthority("aa");
		ss.save(cert);
		
		Reg reg = new Reg();
		reg.setSeafarerId(sf.getId());
		reg.setSeqNo(sf.getSeqNo());
		reg = ss.save(reg);
		//Reg renew = ss.renew(reg);
		sf = ss.findById(Seafarer.class, sf.getId());
		ss.reIssueSerb(sf, "test", new Date());
		String newSerbNo;
		String seafarerId;
		PreviousSerb latestPS = previousSerbDao.findLatestBySeafarerId(sf.getId());
		if(latestPS!=null){
			int nextSeqNo = latestPS.getSeqNo() + 1;
			ss.reIssueSerb(sf.getId(), String.valueOf(nextSeqNo), new Date());
		}
	}

}
