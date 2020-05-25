package org.mardep.ssrs.reports;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.inbox.ITaskDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IPreReservedNameDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.inbox.Task;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.CsrForm;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.PreReservedName;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.ICsrService;
import org.mardep.ssrs.service.IDeRegService;
import org.mardep.ssrs.service.IInboxService;
import org.mardep.ssrs.service.IShipRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class ShipRegServiceTest {
	@Autowired
	IShipRegService srService;
	@Autowired
	IInboxService inbox;

	@Autowired
	ITaskDao taskDao;

	@Autowired
	IPreReservedNameDao preReservedNameDao;

	@Autowired
	IOwnerDao ownerDao;
	@Autowired
	IRegMasterDao rmDao;
	@Autowired
	IDeRegService dereg;
	@Autowired
	ICsrService csr;
	private RegMaster rm;

	@Before
	public void init() {
		User user = new User();
		user.setId("TESTER");
		UserContextThreadLocalHolder.setCurrentUser(user);
		rm = rmDao.findAll().get(0);
	}

	@Test
	@Transactional()
	@Rollback(false)
	public void testOwnerChange() {
		RegMaster entity = new RegMaster();
		entity.setRegName("Test " + System.currentTimeMillis());
		ApplDetail details = new ApplDetail();
		Owner owner = new Owner();
		owner.setName(entity.getRegName() + " on1");
		List<Owner> owners = Arrays.asList(owner);
		Representative rep = new Representative();
		rep.setName(entity.getRegName() + " rp");
		rep.setStatus(Representative.STATUS_CORPORATION);

		RegMaster result = srService.create(entity, details, owners, rep);


		srService.receiveOwnerChange(result.getApplNo(), 1);

		Task criteria = new Task();

		criteria.setParam1(result.getApplNo());
		criteria.setName("ownerChange_received");
		List<Task> tasks = taskDao.findByCriteria(criteria);

		srService.acceptOwnerChange(tasks.get(0).getId());
		criteria.setName("ownerChange_accepted");
		tasks = taskDao.findByCriteria(criteria);
		srService.approveOwnerChange(tasks.get(0).getId());
		criteria.setName("ownerChange_approved");
		tasks = taskDao.findByCriteria(criteria);
		Transaction txn = new Transaction();
		txn.setApplNo(entity.getApplNo());
		txn.setHourChange("00");
		txn.setDateChange(new Date());
		Owner ownerCriteria = new Owner();
		ownerCriteria.setApplNo(result.getApplNo());
		ownerCriteria.setOwnerSeqNo(1);
		owners = ownerDao.findByCriteria(ownerCriteria);
		srService.completeOwnerChange(owners.get(0), tasks.get(0).getId(), txn);
	}
	@Test
	@Transactional()
	@Rollback(false)
	public void testWithdrawOwnerChange() {
		RegMaster entity = new RegMaster();
		entity.setRegName("Test " + System.currentTimeMillis());
		ApplDetail details = new ApplDetail();
		Owner owner = new Owner();
		owner.setName(entity.getRegName() + " on1");
		List<Owner> owners = Arrays.asList(owner);
		Representative rep = new Representative();
		rep.setName(entity.getRegName() + " rp");
		rep.setStatus(Representative.STATUS_CORPORATION);

		RegMaster result = srService.create(entity, details, owners, rep);


		srService.receiveOwnerChange(result.getApplNo(), 1);

		Task criteria = new Task();

		criteria.setParam1(result.getApplNo());
		criteria.setName("ownerChange_received");
		List<Task> tasks = taskDao.findByCriteria(criteria);

		srService.acceptOwnerChange(tasks.get(0).getId());
		criteria.setName("ownerChange_accepted");
		tasks = taskDao.findByCriteria(criteria);
		srService.withdrawOwnerChange(tasks.get(0).getId());
	}
	@Test
	@Transactional()
	@Rollback(false)
	public void testOwner() {
		RegMaster entity = new RegMaster();
		entity.setRegName("Test " + System.currentTimeMillis());
		ApplDetail details = new ApplDetail();
		Owner owner = new Owner();
		owner.setName(entity.getRegName() + " on1");
		List<Owner> owners = Arrays.asList(owner);
		Representative rep = new Representative();
		rep.setName(entity.getRegName() + " rp");
		rep.setStatus(Representative.STATUS_CORPORATION);

		RegMaster result = srService.create(entity, details, owners, rep);

		owner.setName(entity.getRegName() + " on2");
		srService.createOwners(Arrays.asList(owner));

		owners = ownerDao.findByApplId(entity.getApplNo());
		owners.get(0).setAddress1("Room 1000");
		Transaction txn = new Transaction();
		txn.setApplNo(entity.getApplNo());
		txn.setHourChange("00");
		txn.setDateChange(new Date());
		srService.updateOwner(owners.get(0), txn);
	}

	@Test
	public void testGetOwnerByImo() {
		List<Owner> owners = ownerDao.findByImo("9254719");
		System.out.println(owners);
	}

	@Test
	@Transactional()
	@Rollback(false)
	public void testWithdraw() throws AddressException, MessagingException {
		java.util.logging.Logger.getLogger("ShipRegServiceTest").info("testShipReg start");

		RegMaster entity = new RegMaster();
		entity.setRegName("Test " + System.currentTimeMillis());
		ApplDetail details = new ApplDetail();
		Owner owner = new Owner();
		owner.setName(entity.getRegName() + " on1");
		List<Owner> owners = Arrays.asList(owner);
		Representative rep = new Representative();
		rep.setName(entity.getRegName() + " rp");
		rep.setStatus(Representative.STATUS_CORPORATION);

		RegMaster result;
		List<Task> tasks;
		Task criteria = new Task();
		result = srService.create(entity, details, owners, rep);
		criteria.setParam1(result.getApplNo());
		criteria.setName("newShipReg_received");
		tasks = taskDao.findByCriteria(criteria);
		srService.withdraw(result, tasks.get(0).getId(), true);
	}
	@Test
	@Transactional()
	@Rollback(false)
	public void testShipReg() throws AddressException, MessagingException, InterruptedException, ParseException {
		java.util.logging.Logger.getLogger("ShipRegServiceTest").info("testShipReg start");

		List<Owner> owners = newShipReg();

		RegMaster check = new RegMaster();
		check.setRegName("xxx");
		check.setRegChiName("xxx");
		srService.check(check, owners);
		for (PreReservedName name: preReservedNameDao.findAll()) {
			check = new RegMaster();
			check.setRegName(name.getName());
			check.setRegChiName(name.getName());
			RegMaster checkResult = srService.check(check, owners);
			System.out.println(checkResult.getRegName());
			checkResult = srService.check(check, owners);
			System.out.println(checkResult.getRegChiName());
			break;
		}
	}

	@Test
	public void testNewShip() throws InterruptedException, ParseException {
		for (int i = 0; i < 20; i++) {
			newShipReg();
		}
	}
	public List<Owner> newShipReg() throws InterruptedException, ParseException {
		RegMaster entity = new RegMaster();
		Thread.sleep(1000);
		entity.setRegName("KEBS Test T " + new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
		ApplDetail details = new ApplDetail();
		Owner owner = new Owner();
		owner.setName(entity.getRegName() + " on1");
		List<Owner> owners = Arrays.asList(owner);
		Representative rep = new Representative();
		rep.setName(entity.getRegName() + " rp");
		entity.setOffNo("HK-0001");
		entity.setShipTypeCode("CGO");
		entity.setRegNetTon(new BigDecimal("9998"));
		entity.setRegDate(new SimpleDateFormat("yyyyMMddHHmm").parse("201601010000"));
		rep.setStatus(Representative.STATUS_CORPORATION);

		RegMaster result;
		List<Task> tasks;
		Task criteria = new Task();
		result = srService.create(entity, details, owners, rep);
		criteria.setParam1(result.getApplNo());
		criteria.setName("newShipReg_received");
		tasks = taskDao.findByCriteria(criteria);

		assert (!tasks.isEmpty()) ;
		RegMaster requesting = srService.requestAccept(result, Arrays.asList(owner), tasks.get(0).getId());
		criteria.setName("newShipReg_pendingAccept");
		tasks = taskDao.findByCriteria(criteria);
		RegMaster accepted = srService.accept(requesting, tasks.get(0).getId());
		// ready
		criteria.setName("newShipReg_pendingDoc");
		tasks = taskDao.findByCriteria(criteria);
		RegMaster ready = srService.approveReady(accepted, tasks.get(0).getId());
		// corReady
		criteria.setName("newShipReg_ready");
		tasks = taskDao.findByCriteria(criteria);
		RegMaster corReady = srService.corReady(ready, tasks.get(0).getId());
		// approve
		criteria.setName("newShipReg_pendingPda");
		tasks = taskDao.findByCriteria(criteria);
		RegMaster approved = srService.approve(corReady, tasks.get(0).getId());
		// complete
		criteria.setName("newShipReg_readyIssueDM");
		tasks = taskDao.findByCriteria(criteria);
		RegMaster completed = srService.complete(approved, tasks.get(0).getId());
		return owners;
	}

	@Test
	@Transactional
	@Rollback(false)
	public void testDeReg() {
		boolean reReg = true;
		String applNo = rm.getApplNo();
		Task[] receive = dereg.receive(applNo, reReg);
		Task[] accept = dereg.accept(rm, receive[0].getId(), reReg);
		Task[] approve = dereg.approve(accept[0].getId(), reReg);
		dereg.withdraw(approve[0].getId(), reReg);
	}

	@Test
	@Transactional
	public void testCsr() {
		CsrForm last = csr.getLast(rm.getImoNo());
		CsrForm form = new CsrForm();
		form.setShipName(rm.getRegName());
		form.setOwners(new ArrayList<>());
		form.setApplNo(rm.getApplNo());
		form.setImoNo(rm.getImoNo());
		form.setFormSeq(last == null ? 1 : last.getFormSeq() + 1);

		csr.save(form);
	}

	@Test
	@Rollback(false)
	public void testSaveHistory() throws ParseException {
		Date txDate = new SimpleDateFormat("yyyyMMdd").parse("20190721");
		Transaction transaction = new Transaction();
		transaction.setDateChange(txDate);
		transaction.setHourChange("0000");

		srService.saveHistory("2019/346", transaction);

		RegMaster findById = srService.findById("2019/346", new Date());
		assert (findById != null);
	}
}
