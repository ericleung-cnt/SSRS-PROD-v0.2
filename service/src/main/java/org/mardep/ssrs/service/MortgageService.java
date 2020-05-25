package org.mardep.ssrs.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrMortgageDao;
import org.mardep.ssrs.dao.sr.IMortgageDao;
import org.mardep.ssrs.dao.sr.IMortgageeDao;
import org.mardep.ssrs.dao.sr.IMortgagorDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.ITransactionDao;
import org.mardep.ssrs.domain.inbox.Task;
import org.mardep.ssrs.domain.sr.Amendment;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.MortgagePK;
import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.domain.sr.Mortgagor;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TXN_CODE	TC_DESC 37	CHANGE OF MORTGAGE DETAILS WITH RESPECT TO OWNERSHIP
 * @author Leo.LIANG
 *
 */
@Service
@Transactional
public class MortgageService extends AbstractService implements IMortgageService {

	@Autowired
	protected IOcrMortgageDao ocrMortgageDao;

	@Autowired private IInboxService inbox;

	@Autowired
	IShipRegService srService;

	@Override
	public Mortgage add(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees) {
		mortgage.setMortStatus(Mortgage.STATUS_RECEIVED);
		Mortgage result = persists(mortgage, mortgagors, mortgagees);
		inbox.startWorkflow("registerMortgage", mortgage.getApplNo(), mortgage.getPriorityCode(), "", "");
		return result;
	}
	private Mortgage persists(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees) {
		IOwnerDao ownerDao = (IOwnerDao) getDao(Owner.class);
		IMortgageDao mortgageDao = (IMortgageDao) getDao(Mortgage.class);
		IMortgagorDao mortgagorDao = (IMortgagorDao) getDao(Mortgagor.class);
		IMortgageeDao mortgageeDao = (IMortgageeDao) getDao(Mortgagee.class);

		Mortgage result = mortgageDao.save(mortgage);
		if (mortgagors != null && mortgagees != null) {
			List<Owner> owners = ownerDao.findByApplId(mortgage.getApplNo());
			List<String> currentMortgagors = mortgagorDao.findMortgagorNames(result.getApplNo(), result.getPriorityCode());
			if (result.getVersion() != 0) {
				currentMortgagors.forEach(m -> {
					if (!mortgagors.contains(m)) {
						mortgagorDao.deleteByName(result.getApplNo(), result.getPriorityCode(), m);
					}
				});
			}
			mortgagors.forEach(m -> {
				if (!currentMortgagors.contains(m)) {
					Mortgagor entity = new Mortgagor();
					entity.setApplNo(mortgage.getApplNo());
					entity.setPriorityCode(mortgage.getPriorityCode());
					for (Owner owner : owners) {
						if (owner.getName().equals(m)) {
							entity.setSeq(owner.getOwnerSeqNo());
							break;
						}
					}
					mortgagorDao.save(entity);
				}
			});
			int[] mSeq = new int[1];
			mortgagees.forEach(m -> {
				m.setApplNo(mortgage.getApplNo());
				m.setPriorityCode(mortgage.getPriorityCode());
				m.setSeq(mSeq[0]);
				mSeq[0] = mSeq[0] + 1;
				mortgageeDao.save(m);
			});
		}
		return result;
	}
	@Override
	public List<Mortgagee> getMortgagees(String applNo, String code) {
		IMortgageeDao dao = (IMortgageeDao) getDao(Mortgagee.class);
		Mortgagee criteria = new Mortgagee();
		criteria.setApplNo(applNo);
		criteria.setPriorityCode(code);
		return dao.findByCriteria(criteria);
	}
	@Override
	public List<String> getMortgagors(String applNo, String code) {
		IMortgagorDao dao = (IMortgagorDao) getDao(Mortgagor.class);
		return dao.findMortgagorNames(applNo, code);
	}

	@Override
	public Mortgage receiveNewMortgageApplication(Mortgage mortgage) {
		Task[] tasks = inbox.startWorkflow("registerMortgage", mortgage.getApplNo(), "", "", "");
		if (tasks != null && tasks.length == 1) {
			mortgage = acceptReg(mortgage, null, null, tasks[0].getId());
		}
		return mortgage;
	}

	@Override
	public Mortgage acceptReg(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId) {
		mortgage.setMortStatus(Mortgage.STATUS_PENDING);
		IMortgageDao dao = (IMortgageDao) getDao(Mortgage.class);
		char next = dao.nextPriority(mortgage.getApplNo());
		mortgage.setPriorityCode(""+next);

		Mortgage result = persists(mortgage, mortgagors, mortgagees);
		Task[] tasks = inbox.proceed(taskId, "acceptRegisterMortgage", "");
		for (Task task : tasks) {
			task.setParam2(mortgage.getPriorityCode());
			inbox.save(task);
		}
		return result;
	}
	@Override
	public Mortgage approveReg(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId) {
		mortgage.setMortStatus(Mortgage.STATUS_APPROVED);
		Mortgage result = persists(mortgage, mortgagors, mortgagees);
		inbox.proceed(taskId, "approveRegisterMortgage", "");
		return result;
	}
	@Override
	public Mortgage completeReg(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId, Transaction tx) throws ParseException {
		mortgage.setMortStatus(Mortgage.STATUS_ACTIVE);
		long dateChg = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(tx.getDateChange()));
		String regTimeStr = Long.toString(dateChg * 10000 + Integer.parseInt(tx.getHourChange()));
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		Date regTime = format.parse(regTimeStr);
		mortgage.setRegTime(regTime);
		Mortgage result = persists(mortgage, mortgagors, mortgagees);
		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
		tx.setPriorityCode(mortgage.getPriorityCode());
		tx = txDao.save(mortgage.getApplNo(), Transaction.CODE_REG_MORTGAGE, tx);
		srService.saveHistory(mortgage.getApplNo(), tx);

		inbox.proceed(taskId, "completeRegisterMortgage", "");
		return result;
	}
	@Override
	public Mortgage withdrawReg(Mortgage mortgage, Long taskId) {
		mortgage.setMortStatus(Mortgage.STATUS_WITHDRAW);
		Mortgage result = persists(mortgage, null, null);
		inbox.proceed(taskId, "withdrawRegisterMortgage", "");
		return result;
	}
	@Override
	public Mortgage acceptDischarge(Mortgage mortgage, Long taskId, boolean discharge) {
		inbox.proceed(taskId, discharge ? "acceptDischargeMortgage" : "acceptCancelMortgage", "");
		return mortgage;
	}
	@Override
	public Mortgage approveDischarge(Mortgage mortgage, Long taskId, boolean discharge) {
		inbox.proceed(taskId, discharge ? "approveDischargeMortgage" : "approveCancelMortgage", "");
		return mortgage;
	}
	@Override
	public Mortgage completeDischarge(Mortgage mortgage, Long taskId, boolean discharge, Transaction tx) {
		mortgage.setMortStatus(discharge ? Mortgage.STATUS_DISCHARGED : Mortgage.STATUS_CANCELLED);
		Mortgage result = persists(mortgage, null, null);
		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
		tx.setPriorityCode(mortgage.getPriorityCode());
		tx = txDao.save(mortgage.getApplNo(), discharge ? Transaction.CODE_DISCHARGE_MORTGAGE : Transaction.CODE_CANCEL_MORTGAGE, tx);
		srService.saveHistory(mortgage.getApplNo(), tx);
		inbox.proceed(taskId, discharge ? "completeDischargeMortgage" : "completeCancelMortgage", "");
		return result;
	}
	@Override
	public Mortgage withdrawDischarge(Long taskId, boolean discharge) {
		inbox.proceed(taskId, discharge ? "withdrawDischargeMortgage" : "withdrawCancelMortgage", "");
		return null;
	}
	@Override
	public Mortgage receiveDischarge(String applNo, String code, boolean discharge) {
		inbox.startWorkflow(discharge ? "dischargeMortgage" : "cancelMortgage", applNo, code, "", "");
		return null;
	}
	@Override
	public Mortgage receiveTransfer(String applNo, String code) {
		inbox.startWorkflow("transferMortgage", applNo, code, "", "");
		Mortgage criteria = new Mortgage();
		MortgagePK pk = new MortgagePK(applNo, code);
		return (Mortgage) getDao(Mortgage.class).findById(pk);
	}
	@Override
	public Mortgage acceptTransfer(Mortgage mortgage, Long taskId) {
		inbox.proceed(taskId, "acceptTransferMortgage", "");
		return mortgage;
	}
	@Override
	public Mortgage approveTransfer(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId) {
		inbox.proceed(taskId, "approveTransferMortgage", "");
		return mortgage;
	}
	@Override
	public Mortgage completeTransfer(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId, Transaction tx) {
		tx.setCode(Transaction.CODE_TRANSFER_MORTGAGE);
		tx.setPriorityCode(mortgage.getPriorityCode());
		return changeMortgagees(mortgage, mortgagors, mortgagees, taskId, tx, "completeTransferMortgage");
	}
	@Override
	public Mortgage amendMortgagees(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Amendment amm) {
		String priorityCode = mortgage.getPriorityCode();

		IMortgageeDao mortgageeDao = (IMortgageeDao) getDao(Mortgagee.class);
		Mortgagee criteria = new Mortgagee();
		criteria.setApplNo(mortgage.getApplNo());
		criteria.setPriorityCode(mortgage.getPriorityCode());
		List<Mortgagee> existing = mortgageeDao.findByCriteria(criteria);
		for (int i = 0; i < mortgagees.size(); i++) {
			Mortgagee m = mortgagees.get(i);
			if (m.getVersion() != null) {
				// update
				for (int j = 0; j < existing.size(); j++) {
					if (existing.get(j).getSeq().equals(m.getSeq())) {
						mortgageeDao.save(m);
						existing.remove(j);
						break;
					}
				}
			} else {
				// insert
				m.setSeq(existing.size() + i);
				m.setApplNo(mortgage.getApplNo());
				m.setPriorityCode(mortgage.getPriorityCode());
				mortgageeDao.save(m);
			}
		}
		IMortgagorDao mgorDao = (IMortgagorDao) getDao(Mortgagor.class);
		List<Mortgagor> exMortgagors = mgorDao.findByCriteria(mortgage);
		String names = "";
		for (Mortgagor gor : exMortgagors) {
			if (!mortgagors.contains(gor.getOwner().getName())) {
				mgorDao.delete(gor);
			} else {
				names += gor.getOwner().getName() + "\n";
			}
		}
		IOwnerDao odao = (IOwnerDao) getDao(Owner.class);
		List<Owner> owners = odao.findByApplId(mortgage.getApplNo());
		for (String name : mortgagors) {
			if (!names.contains(name)) {
				Mortgagor mortgagor = new Mortgagor();
				mortgagor.setApplNo(mortgage.getApplNo());
//				String priorityCode = mortgage.getPriorityCode();
				mortgagor.setPriorityCode(priorityCode);
				for (Owner own : owners) {
					if (own.getName().equals(name)) {
						mortgagor.setSeq(own.getOwnerSeqNo());
						break;
					}
				}
				mgorDao.save(mortgagor);
			}
		}

		for (Mortgagee m:existing) {
			mortgageeDao.delete(m);
		}
		IMortgageDao mdao = (IMortgageDao) getDao(Mortgage.class);
		mdao.save(mortgage);

		srService.saveHistory(mortgage.getApplNo(), amm);
		return mortgage;
	}
	private Mortgage changeMortgagees(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId, Transaction tx,
			String action) {
		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
		if (tx != null) {
			tx.setPriorityCode(mortgage.getPriorityCode());
			tx = txDao.save(mortgage.getApplNo(), tx.getCode(), tx);
		}
		IMortgageeDao mortgageeDao = (IMortgageeDao) getDao(Mortgagee.class);
		Mortgagee criteria = new Mortgagee();
		criteria.setApplNo(mortgage.getApplNo());
		criteria.setPriorityCode(mortgage.getPriorityCode());
		List<Mortgagee> existing = mortgageeDao.findByCriteria(criteria);
		for (int i = 0; i < mortgagees.size(); i++) {
			Mortgagee m = mortgagees.get(i);
			if (m.getVersion() != null) {
				// update
				for (int j = 0; j < existing.size(); j++) {
					if (existing.get(j).getSeq().equals(m.getSeq())) {
						mortgageeDao.save(m);
						existing.remove(j);
						break;
					}
				}
			} else {
				// insert
				m.setSeq(existing.size() + i);
				m.setApplNo(mortgage.getApplNo());
				m.setPriorityCode(mortgage.getPriorityCode());
				mortgageeDao.save(m);
			}
		}
		IMortgagorDao mgorDao = (IMortgagorDao) getDao(Mortgagor.class);
		List<Mortgagor> exMortgagors = mgorDao.findByCriteria(mortgage);
		String names = "";
		for (Mortgagor gor : exMortgagors) {
			if (!mortgagors.contains(gor.getOwner().getName())) {
				mgorDao.delete(gor);
			} else {
				names += gor.getOwner().getName() + "\n";
			}
		}
		IOwnerDao odao = (IOwnerDao) getDao(Owner.class);
		List<Owner> owners = odao.findByApplId(mortgage.getApplNo());
		for (String name : mortgagors) {
			if (!names.contains(name)) {
				Mortgagor mortgagor = new Mortgagor();
				mortgagor.setApplNo(mortgage.getApplNo());
				mortgagor.setPriorityCode(mortgage.getPriorityCode());
				for (Owner own : owners) {
					if (own.getName().equals(name)) {
						mortgagor.setSeq(own.getOwnerSeqNo());
						break;
					}
				}
				mgorDao.save(mortgagor);
			}
		}

		for (Mortgagee m:existing) {
			mortgageeDao.delete(m);
		}
		IMortgageDao mdao = (IMortgageDao) getDao(Mortgage.class);
		mdao.save(mortgage);

		srService.saveHistory(mortgage.getApplNo(), tx);
		if (taskId != null) {
			inbox.proceed(taskId, action, "");
		}
		return mortgage;
	}
	@Override
	public Mortgage withdrawTransfer(Long taskId) {
		inbox.proceed(taskId, "withdrawTransferMortgage", "");
		return null;
	}
	@Override
	public Mortgage receiveDetailChange(String applNo, String code) {
		inbox.startWorkflow("mortgageDetails", applNo, code, "", "");
		return null;
	}
	@Override
	public Mortgage acceptDetailChange(Mortgage mortgage, Long taskId) {
		inbox.proceed(taskId, "acceptMortgageDetails", "");
		return mortgage;
	}
	@Override
	public Mortgage approveDetailChange(Mortgage mortgage, Long taskId) {
		inbox.proceed(taskId, "approveMortgageDetails", "");
		return mortgage;
	}
	@Override
	public Mortgage completeDetailChange(Mortgage mortgage, Long taskId, Transaction tx) {
		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
		tx.setPriorityCode(mortgage.getPriorityCode());
		tx = txDao.save(mortgage.getApplNo(), Transaction.CODE_MORTGAGE_DETAIL, tx);
		IMortgageDao mDao = (IMortgageDao) getDao(Mortgage.class);
		mDao.save(mortgage);
		srService.saveHistory(mortgage.getApplNo(), tx);
		inbox.proceed(taskId, "completeMortgageDetails", "");
		return mortgage;
	}
	@Override
	public Mortgage withdrawDetailChange(Long taskId) {
		inbox.proceed(taskId, "withdrawMortgageDetails", "");
		return null;
	}
	@Override
	public Mortgage receiveMortgageesChange(String applNo, String code) {
		inbox.startWorkflow("mortgageeDetails", applNo, code, "", "");
		return null;
	}
	@Override
	public Mortgage acceptMortgageesChange(Mortgage mortgage, Long taskId) {
		inbox.proceed(taskId, "acceptMortgageeDetails", "");
		return mortgage;
	}
	@Override
	public Mortgage approveMortgageesChange(Mortgage mortgage, Long taskId) {
		inbox.proceed(taskId, "approveMortgageeDetails", "");
		return mortgage;
	}
	@Override
	public Mortgage completeMortgageesChange(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId, Transaction tx) {
		tx.setCode(Transaction.CODE_MORTGAGEES_CHANGE);
		return changeMortgagees(mortgage, mortgagors, mortgagees, taskId, tx, "completeMortgageeDetails");
	}
	@Override
	public Mortgage withdrawMortgageesChange(Long taskId) {
		inbox.proceed(taskId, "withdrawMortgageeDetails", "");
		return null;
	}
	@Override
	public List<Mortgage> findMortgages(String applNo, Date reportDate) {
		return ((IMortgageDao) getDao(Mortgage.class)).findHistory(applNo, reportDate);
	}
	@Override
	public List<Mortgagee> findMortgagees(Mortgage mortgage, Date reportDate) {
		return ((IMortgageeDao) getDao(Mortgagee.class)).findByTime(mortgage, reportDate);
	}
	@Override
	public List<Mortgagor> findMortgagors(Mortgage mortgage, Date reportDate) {
		return ((IMortgagorDao) getDao(Mortgagor.class)).findByTime(mortgage, reportDate);
	}

	@Override
	public List<Mortgage> findMortgagesByApplId(String applNo){
		return ((IMortgageDao) getDao(Mortgage.class)).findByApplId(applNo);
	}

	@Override
	public List<Mortgagee> findMortgageesByMortgage(Mortgage mortgage){
		return ((IMortgageeDao) getDao(Mortgagee.class)).findByMortgage(mortgage);
	}

	@Override
	public List<Mortgagor> findMortgagorsByMortgage(Mortgage mortgage){
		return ((IMortgagorDao) getDao(Mortgagor.class)).findByMortgage(mortgage);
	}

	@Override
	public String nextMortgageCode(String applNo) {
		String mortgageCode = "A";
		Mortgage latestMortgage = ocrMortgageDao.getLatestMortgageByApplNo(applNo);

		if (latestMortgage!=null) {
			int value = latestMortgage.getPriorityCode().charAt(0) + 1;
			mortgageCode = Character.toString((char)value);
		}
		return mortgageCode;
	}
	@Override
	public Map<String, String> findMortgageRegDateNatures(String applNo) {
		IMortgageDao mortgageDao = (IMortgageDao) getDao(Mortgage.class);
		return mortgageDao.findMortgageRegDateNatures(applNo);
	}
}
