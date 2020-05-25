package org.mardep.ssrs.dao.seafarer.impl.test;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.sr.IMortgageDao;
import org.mardep.ssrs.dao.sr.IMortgagorDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagor;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:jpaDao-test.xml")
public class MortgageDaoTest {

	@Autowired IMortgageDao mdao;

	@Autowired IRegMasterDao rmDao;

	@Autowired IOwnerDao ownerDao;

	@Autowired IMortgagorDao mgorDao;

	@Before
	public void init() {
		User user = new User();
		user.setId(this.getClass().getSimpleName());
		UserContextThreadLocalHolder.setCurrentUser(user);
	}

	@Test
	@Transactional
	@Rollback(false)
	public void testMortgage() throws ParseException {
		Mortgage mg = create();
		System.out.println(mg);
		mdao.findByApplId(mg.getApplNo(), mg.getCreatedDate());
	}

	private Mortgage create() throws ParseException {
		RegMasterDaoTest rmTest = new RegMasterDaoTest();
		rmTest.rmDao = rmDao;
		rmTest.ownerDao = ownerDao;
		RegMaster rm = rmTest.create(RegMaster.REG_STATUS_ACTIVE);
		Mortgage mg = new Mortgage();
		mg.setApplNo(rm.getApplNo());
		mg.setPriorityCode("A");
		mg.setHigherMortgageeConsent("Y");
		mg.setMortStatus(Mortgage.STATUS_ACTIVE);

		mg = mdao.save(mg);
		Mortgagor mgor = new Mortgagor();
		mgor.setApplNo(mg.getApplNo());
		mgor.setPriorityCode(mg.getPriorityCode());
		mgor.setSeq(1);
		List<Owner> owners = ownerDao.findByApplId(rm.getApplNo());
		mgor.setOwner(owners.get(0));
		mgorDao.save(mgor);
		mgorDao.findMortgagorNames(mg.getApplNo(), mg.getPriorityCode());


		mgorDao.deleteByName(mg.getApplNo(), mg.getPriorityCode(), "xx");
		mgorDao.findByTime(mg, mg.getCreatedDate());
		mgorDao.findByCriteria(mg);

		ownerDao.findByApplId(rm.getApplNo(), new Date());
		return mg;
	}
}
