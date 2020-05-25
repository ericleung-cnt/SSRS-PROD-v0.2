package org.mardep.ssrs.dao.seafarer.impl.test;

import java.util.Calendar;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.sr.ICsrFormDao;
import org.mardep.ssrs.dao.sr.ICsrFormOwnerDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.sr.CsrForm;
import org.mardep.ssrs.domain.sr.CsrFormOwner;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
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
public class CsrFormDaoTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ICsrFormDao formDao;

	@Autowired
	private IRegMasterDao regDao;

	@Autowired
	private ICsrFormOwnerDao csrOwnerDao;
	
	@Autowired
	private IOwnerDao ownerDao;

	@Test
	public void testFind() {
		List<CsrForm> list = formDao.findAll();
		System.out.println(list.size());
	}

	@Test
	@Transactional
	@Rollback(false)
	public void testCreate() {
		logger.info("#test create..................");
		User user = new User();
		user.setId(this.getClass().getSimpleName());
		UserContextThreadLocalHolder.setCurrentUser(user);

		String applNo;
		RegMaster newRegMaster = new RegMaster();
		
		int thisYear = Calendar.getInstance().get(Calendar.YEAR);
		String max = regDao.maxApplNo(thisYear);
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
		
		newRegMaster.setApplNo(applNo);
		newRegMaster.setRegName("TEST ship");
		newRegMaster.setImoNo("9999999");
		newRegMaster = regDao.save(newRegMaster);
		Owner owner = new Owner();
		owner.setApplNo(applNo);
		owner.setOwnerSeqNo(1);
		owner.setName("XYZ");
		ownerDao.save(owner);
		String applNoSuf = "Y";
		String flagState = "!"; // ??
		CsrForm target = new CsrForm();
		target.setApplNo(applNo);
		target.setFlagState(flagState);

		
		RegMaster regMaster = new RegMaster();
		regMaster.setApplNo(target.getApplNo());
		List<RegMaster> regs = regDao.findByCriteria(regMaster);
		if (!regs.isEmpty()) {
			regMaster = regs.get(0);
		} else {
			throw new IllegalArgumentException("no such registration " + applNo + "," + applNoSuf);
		}

		target.setApplNo(regMaster.getApplNo());
		target.setImoNo(regMaster.getImoNo());
		target.setFormApplyDate(regMaster.getRegDate());
		target.setRegistrationDate(regMaster.getRegDate());
		target.setShipName(regMaster.getRegName());

		List<CsrForm> list = formDao.findByCriteria(target);
		int seq = 1;
		if (!list.isEmpty()) {
			list.sort((a, b) -> {
				return -a.getFormSeq().compareTo(b.getFormSeq());
			});
			CsrForm last = list.get(0);
			seq = last.getFormSeq() + 1;

			target.setShipManager(last.getShipManager());
			target.setShipManagerAddress1(last.getShipManagerAddress1());
			target.setShipManagerAddress2(last.getShipManagerAddress2());
			target.setShipManagerAddress3(last.getShipManagerAddress3());

			target.setSafetyActAddress1(last.getSafetyActAddress1());
			target.setSafetyActAddress2(last.getSafetyActAddress2());
			target.setSafetyActAddress3(last.getSafetyActAddress3());

			target.setClassSocietyId(last.getClassSocietyId());
			target.setClassSociety2(last.getClassSociety2());

		}
		target.setFormSeq(seq);
		formDao.save(target);

		CsrFormOwner newOwner = new CsrFormOwner();
		newOwner.setImoNo(target.getImoNo());
		newOwner.setFormSeq(seq);
		if (seq > 1) {
			CsrFormOwner ownerCriteria = new CsrFormOwner();
			ownerCriteria.setFormSeq(seq - 1);
			ownerCriteria.setImoNo(target.getImoNo());
			List<CsrFormOwner> owners = csrOwnerDao.findByCriteria(ownerCriteria);
			if (!owners.isEmpty()) {
				CsrFormOwner lastOwner = owners.get(0);
				newOwner.setCompanyId(lastOwner.getCompanyId());
			}
			// send email to inform applicant about application recieved
			// send email to applicant to collect CSR
		}

		csrOwnerDao.save(newOwner);
	}

}
