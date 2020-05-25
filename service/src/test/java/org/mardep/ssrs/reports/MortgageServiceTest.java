package org.mardep.ssrs.reports;

import java.text.ParseException;
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
import org.mardep.ssrs.dao.sr.IMortgageDao;
import org.mardep.ssrs.dao.sr.IMortgageeDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IPreReservedNameDao;
import org.mardep.ssrs.domain.inbox.Task;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.MortgagePK;
import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IInboxService;
import org.mardep.ssrs.service.IMortgageService;
import org.mardep.ssrs.service.IShipRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class MortgageServiceTest {
	@Autowired
	IShipRegService srService;
	@Autowired
	IInboxService inbox;

	@Autowired
	ITaskDao taskDao;

	@Autowired
	IPreReservedNameDao preReservedNameDao;

	@Autowired
	IMortgageService ms;

	@Autowired
	IOwnerDao ownerDao;

	@Autowired
	IMortgageDao mortgageDao;

	@Autowired
	IMortgageeDao mortgageeDao;
	private Owner owner;
	private String ownerName;

	@Before
	public void init() {
		User user = new User();
		user.setId("TESTER");
		UserContextThreadLocalHolder.setCurrentUser(user);
		owner = ownerDao.findAll().get(0);
		ownerName = owner.getName();

	}
	@Test
	@Transactional()
	@Rollback(false)
	public void testMortgage() throws AddressException, MessagingException, ParseException {
		List<String> mortgagors = Arrays.asList(ownerName);

		Mortgage saved = saveMortgage(owner, mortgagors);

		Long id = findTaskId(saved, "registerMortgage_received");

		List<Mortgagee> mortgagees = mortgageeDao.findByCriteria(saved);

		Mortgage accepted = ms.acceptReg(saved, mortgagors, mortgagees, id);
		id = findTaskId(saved, "registerMortgage_accepted");
		Mortgage approved = ms.approveReg(accepted, mortgagors, mortgagees, id);
		id = findTaskId(saved, "registerMortgage_approved");
		Transaction tx = new Transaction();
		tx.setApplNo(saved.getApplNo());
		tx.setCode("33");
		tx.setHourChange("18");
		tx.setDateChange(new Date());
		Mortgage completed = ms.completeReg(accepted, mortgagors, mortgagees, id, tx);

		Mortgage receiveTransfer = ms.receiveTransfer(saved.getApplNo(), completed.getPriorityCode());
		id = findTaskId(saved, "transferMortgage_received");
		Mortgage acceptedTransfer = ms.acceptTransfer(receiveTransfer, id);
		id = findTaskId(saved, "transferMortgage_accepted");
		Mortgage approvedTransfer = ms.approveTransfer(acceptedTransfer, mortgagors, mortgagees, id);
		id = findTaskId(saved, "transferMortgage_approved");

		mortgagees = mortgageeDao.findByCriteria(approvedTransfer);
		Mortgage completedTransfer = ms.completeTransfer(approvedTransfer, mortgagors, mortgagees, id, tx);

	}
	private Long findTaskId(Mortgage saved, String name) {
		Task task = new Task();
		task.setParam1(saved.getApplNo());
		task.setParam2(saved.getPriorityCode());
		task.setName(name);
		task = taskDao.findByCriteria(task).get(0);
		Long id = task.getId();
		return id;
	}
	private Mortgage saveMortgage(Owner owner, List<String> mortgagors) {
		Mortgage criteria = new Mortgage();
		criteria.setApplNo(owner.getApplNo());
		List<Mortgage> mortgages = mortgageDao.findByCriteria(criteria);
		String maxPriorityExists = "@";
		for (Mortgage m : mortgages) {
			if (m.getPriorityCode().charAt(0) > maxPriorityExists.charAt(0)) {
				maxPriorityExists = m.getPriorityCode();
			}
		}

		Mortgage mortgage = criteria;
		mortgage.setApplNo(owner.getApplNo());
		mortgage.setPriorityCode(String.valueOf((char)(maxPriorityExists.charAt(0) + 1)));

		Mortgagee mortgagee = new Mortgagee();
		mortgagee.setApplNo(mortgage.getApplNo());
		mortgagee.setName("testMortgage");
		List<Mortgagee> mortgagees = Arrays.asList(mortgagee);
		Mortgage saved = ms.add(mortgage, mortgagors, mortgagees);
		return saved;
	}

	@Test
	@Transactional()
	public void testDetailChange() {
		List<String> mortgagors = Arrays.asList(ownerName);

		Mortgage saved = saveMortgage(owner, mortgagors);

		Long taskId;
		ms.receiveDetailChange(saved.getApplNo(), saved.getPriorityCode());
		taskId = findTaskId(saved, "mortgageDetails_received");
		ms.acceptDetailChange(saved, taskId);
		taskId = findTaskId(saved, "mortgageDetails_accepted");
		ms.approveDetailChange(saved, taskId);
		taskId = findTaskId(saved, "mortgageDetails_approved");
		Transaction tx = new Transaction();
		tx.setHourChange("00");
		tx.setDateChange(new Date());
		tx.setApplNo(saved.getApplNo());
		tx.setDetails("xx");
		ms.completeDetailChange(saved, taskId, tx);
	}

	@Test
	@Transactional()
	public void testGet() {
		String code = "A";
		String applNo = owner.getApplNo();
		ms.getMortgagees(applNo, code);
		ms.getMortgagors(applNo, code);
		Date reportDate = new Date();
		List<Mortgage> mortgages = ms.findMortgages(applNo, reportDate);
		Mortgage mortgage = mortgages.get(0);
		ms.findMortgagees(mortgage, reportDate);
		ms.findMortgagors(mortgage, reportDate);
	}

	@Test
	@Transactional()
	public void testTransfer() {
		String code = "A";
		String applNo = owner.getApplNo();
		Mortgage m = mortgageDao.findById(new MortgagePK(applNo, code));
		ms.receiveTransfer(applNo, code);
		Long recevied = findTaskId(m, "transferMortgage_received");
		ms.acceptTransfer(m, recevied);
		Long accepted = findTaskId(m, "transferMortgage_accepted");
		ms.approveTransfer(m, null, null, accepted);
		Long approved = findTaskId(m, "transferMortgage_approved");
		ms.withdrawTransfer(approved);
	}
	@Test
	@Transactional()
	public void testDischarge() {
		String code = "A";
		String applNo = owner.getApplNo();
		Mortgage m = mortgageDao.findById(new MortgagePK(applNo, code));
		ms.receiveDischarge(applNo, code, true);
		Long recevied = findTaskId(m, "dischargeMortgage_received");
		ms.acceptDischarge(m, recevied, true);
		Long accepted = findTaskId(m, "dischargeMortgage_accepted");
		ms.approveDischarge(m, accepted, true);
		Long approved = findTaskId(m, "dischargeMortgage_approved");
		ms.withdrawDischarge(approved, true);
	}
	@Test
	@Transactional()
	public void testMortgageesChange() {
		String code = "A";
		String applNo = owner.getApplNo();
		Mortgage m = mortgageDao.findById(new MortgagePK(applNo, code));
		Mortgage mortgage = ms.receiveMortgageesChange(applNo, code);
		Long recevied = findTaskId(m, "mortgageeDetails_received");
		ms.acceptMortgageesChange(mortgage, recevied);
		Long accepted = findTaskId(m, "mortgageeDetails_accepted");
		ms.approveMortgageesChange(mortgage, accepted);
		Long approved = findTaskId(m, "mortgageeDetails_approved");
		ms.withdrawMortgageesChange(approved);

	}

}
