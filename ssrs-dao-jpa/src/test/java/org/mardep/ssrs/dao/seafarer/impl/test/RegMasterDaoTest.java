package org.mardep.ssrs.dao.seafarer.impl.test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dao.sr.IRepresentativeDao;
import org.mardep.ssrs.domain.codetable.ShipType;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
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
public class RegMasterDaoTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired IRegMasterDao rmDao;

	@Autowired IOwnerDao ownerDao;

	@Autowired IRepresentativeDao repDao;


	@Before
	public void init() {
		User user = new User();
		user.setId(this.getClass().getSimpleName());
		UserContextThreadLocalHolder.setCurrentUser(user);
	}

	@Test
	@Transactional
	@Rollback(false)
	public void testCreate() throws ParseException {
		RegMaster regMaster = create(RegMaster.REG_STATUS_ACTIVE);
		System.out.println(regMaster);
	}

	RegMaster create(String regStatus) throws ParseException {
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
		String applNoSuf = "z";

		RegMaster regMaster = new RegMaster();
		regMaster.setApplNo(applNo);
		regMaster.setApplNoSuf(applNoSuf);
		regMaster.setRegName("x- " + System.currentTimeMillis());

		String maxOffNo = rmDao.maxOffNo();
		if (maxOffNo == null) {
			maxOffNo = "HK-0000";
		}
		String newOffNo;
		if (maxOffNo.length() > 6) {
			newOffNo = "HK-" + String.valueOf(Integer.parseInt(maxOffNo.substring(3)) + 10001).substring(1);
		} else {
			newOffNo = "HK-0001";
		}
		regMaster.setShipTypeCode(ShipType.CARGO_SHIP);
		regMaster.setShipSubtypeCode("CTR");
		regMaster.setOffNo(newOffNo);
		regMaster.setGrossTon(new BigDecimal(9999));
		regMaster.setRegNetTon(new BigDecimal(9999));
		regMaster.setCallSign(rmDao.nextCallSign());
		regMaster.setRegStatus(regStatus);
		regMaster.setRegDate(new SimpleDateFormat("yyyyMMdd").parse("20190101"));
		if (regStatus.equals(regMaster.REG_STATUS_DEREGISTERED)) {
			regMaster.setDeRegTime(new SimpleDateFormat("yyyyMMdd").parse("20190101"));
		} else if (regStatus.equals(RegMaster.REG_STATUS_REGISTERED)) {
		}
		List<RegMaster> regs = rmDao.findByCriteria(regMaster);
		if (regs.isEmpty()) {
			regMaster = rmDao.save(regMaster);
			regMaster.setRegNetTon(new BigDecimal(3000));
			rmDao.save(regMaster);
		}
		Owner owner = new Owner();
		owner.setApplNo(regMaster.getApplNo());
		owner.setOwnerSeqNo(1);
		owner.setName("TEST Owner**");
		owner = ownerDao.save(owner);
		owner.setName("TEST Owner****");
		owner.setAddress1("xxxxx");
		owner = ownerDao.save(owner);
		return regMaster;
	}



	@Test
	@Transactional
	@Rollback(false)
	public void testSearch() {
		List<String> names = Arrays.asList("XZA");
		List<RegMaster> regMasters = rmDao.searchUsedName("regName", names);
		System.out.println(regMasters);
	}


	@Test
	@Transactional
	@Rollback(false)
	public void testReport() throws ParseException {
		RegMaster reg = create(RegMaster.REG_STATUS_REGISTERED);

		update();
		RegMaster deReg  = create(RegMaster.REG_STATUS_DEREGISTERED);
		Date reportDate = new Date();
		List<Map<String,Object>> registrationType = rmDao.getRegistrationType(reportDate);
		rmDao.getBreakDownNoAndGrtOfShipsByType(reportDate);
		rmDao.getShipsByShipTypes(reportDate);
		Date to = new SimpleDateFormat("yyyyMMdd").parse("20180101");
		Date from = new SimpleDateFormat("yyyyMMdd").parse("20191201");
		rmDao.getDeregistered(from, to);
		rmDao.getRegistered(from, to);
		rmDao.getDiscountAtf(from, to);
		rmDao.getNoAndTonnage(reportDate);
		rmDao.getOwnerCatergory(reportDate);
		rmDao.getTonnageDistribution(reportDate);
		rmDao.getCompanyRanking(reportDate);

		Representative rep = repDao.findById("2019/001");
		rep.setAddress2("xxxx");
		repDao.save(rep);
		RegMaster master = rmDao.findById("2019/001");
		master.setRegName("starco");
		rmDao.save(master);
		rmDao.getOwnershipReport(reportDate);
		rmDao.getIntTotAt(reg.getApplNo(), new SimpleDateFormat("yyyyMMdd").parse("20220101"));
	}

	private void update() {
		List<RegMaster> rmList = rmDao.findAll();
		if (!rmList.isEmpty()) {
			for (int i = 0; i < 3; i++) {
				RegMaster entity = rmList.get((int) (Math.random() * rmList.size()));
				entity.setRegNetTon((entity.getRegNetTon() == null ? BigDecimal.ONE : entity.getRegNetTon()).add(BigDecimal.ONE));
				entity.setRegStatus(RegMaster.REG_STATUS_ACTIVE.equals(entity.getRegStatus()) ? RegMaster.REG_STATUS_REGISTERED : RegMaster.REG_STATUS_ACTIVE);
				rmDao.save(entity);
				Representative rep = repDao.findById(entity.getApplNo());
				if (rep == null) {
					rep = repDao.findById("2019/001");
				}
				if (rep != null) {
					String address2 = rep.getAddress2();
					if (address2 == null) {
						address2 = "**";
					}
					rep.setAddress2(rep.getAddress3());
					rep.setAddress3(address2);
					repDao.save(rep);
				}
			}
		}
	}

	@Test
	@Transactional
	@Rollback(false)
	public void testCreateEbsShip() throws ParseException {
		RegMaster criteria = new RegMaster();
		criteria.setApplNo("1985/");
		List<RegMaster> list = rmDao.findByCriteria(criteria);
		list.sort((a,b) -> { return a.getApplNo().compareTo(b.getApplNo()); });
		int last = Integer.parseInt(list.get(list.size() -1).getApplNo().substring(5));

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
		DecimalFormat df = new DecimalFormat("000");
		Date today = new Date();
		int count = 0;
		{
			RegMaster entity = new RegMaster();
			String key = simpleDateFormat.format(new Date());
			entity.setApplNo("1987/333");
			entity.setRegName("TEST EBSSHIP PENDING");
			entity.setRegStatus("A");
			entity.setOffNo("Pend");
			entity.setImoNo("Pend");
			entity.setShipTypeCode("CGO");
			entity.setShipSubtypeCode("BUL");
			entity.setRegDate(simpleDateFormat.parse("130202"));
			entity.setRegNetTon(new BigDecimal("10000"));
			entity.setAtfDueDate(simpleDateFormat.parse(key));
			entity.setCreatedBy("");
			entity.setCreatedDate(today);
			rmDao.save(entity);
		}
		{
			RegMaster entity = new RegMaster();
			String key = simpleDateFormat.format(new Date());
			entity.setApplNo("1987/334");
			entity.setRegName("TEST EBSSHIP SCHEDULED");
			entity.setRegStatus("F");
			entity.setOffNo("sche");
			entity.setImoNo("sche");
			entity.setShipTypeCode("CGO");
			entity.setShipSubtypeCode("BUL");
			entity.setRegDate(simpleDateFormat.parse("270701"));
			entity.setRegDate(simpleDateFormat.parse("130202"));
			entity.setRegNetTon(new BigDecimal("10000"));
			entity.setAtfDueDate(simpleDateFormat.parse(key));
			entity.setCreatedBy("");
			entity.setCreatedDate(today);
			rmDao.save(entity);
		}



		/*for (int i = 0; i < 90; i++) {
			for (int j = 0; j < 3; j++) {
				RegMaster entity = new RegMaster();
				String key = simpleDateFormat.format(new Date(today.getTime() + 24L*3600*1000 * i));
				entity.setApplNo("1986/" + df.format(last + count + 1));
				entity.setRegName("TEST EBSSHIP DUE " + key + "" + count);
				entity.setRegStatus("R");
				entity.setOffNo(key+ count);
				entity.setImoNo(key+ count);
				entity.setShipTypeCode("CGO");
				entity.setShipSubtypeCode("BUL");
				entity.setRegDate(simpleDateFormat.parse("130202"));
				entity.setRegNetTon(new BigDecimal("10000"));
				entity.setAtfDueDate(simpleDateFormat.parse(key));
				entity.setCreatedBy("");
				entity.setCreatedDate(today);
				rmDao.save(entity);
				count++;
			}
		}*/
	}

	@Test
	@Transactional
	@Rollback(false)
	public void testHistory() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		saveHist("1999/002", format.parse("20190101"));
		//saveHist("2019/352", format.parse("20190101"));
	}

	private void saveHist(String applNo, Date txDate) {
		rmDao.saveHistory(applNo , txDate);
		List<RegMaster> list = rmDao.findHistory(applNo, new Date(txDate.getTime() + 60000));
		assert (!list.isEmpty());
		System.out.println(list);
		ownerDao.saveHistory(applNo, txDate);
		repDao.saveHistory(applNo, txDate);
	}
}
