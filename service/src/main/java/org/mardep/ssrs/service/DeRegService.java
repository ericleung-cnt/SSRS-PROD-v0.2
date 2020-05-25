package org.mardep.ssrs.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.codetable.IFeeCodeDao;
import org.mardep.ssrs.dao.inbox.ITaskDao;
import org.mardep.ssrs.dao.sr.IApplDetailDao;
import org.mardep.ssrs.dao.sr.IBuilderMakerDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dao.sr.IRepresentativeDao;
import org.mardep.ssrs.dao.sr.ITransactionDao;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.inbox.Task;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.sr.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeRegService extends AbstractService implements IDeRegService {


	@Autowired
	IInboxService inbox;

	@Autowired
	IShipRegService sr;
	@Autowired
	IDemandNoteService demandNoteService;

	@Override
	public Task[] receive(String applNo, boolean reReg) {
		return inbox.startWorkflow(prefix(reReg), applNo, "", "", "");
	}

	private String prefix(boolean reReg) {
		return reReg ? "reReg" : "deReg";
	}

	@Override
	public Task[] accept(RegMaster rm, Long taskId, boolean reReg) {
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		IApplDetailDao adDao = (IApplDetailDao) getDao(ApplDetail.class);

		String nextApplNo = null;
		if (reReg) {

			RegMaster old = rmDao.findById(rm.getApplNo());
			nextApplNo = sr.nextApplNo();
			logger.info("REREG: new appl no:" + nextApplNo);
			Date now = new Date();
			RegMaster newRm = old.clone();
			newRm.setVersion(null);
			newRm.setApplNo(nextApplNo);
			newRm.setRegStatus(RegMaster.REG_STATUS_ACTIVE);
			newRm.setApplNoSuf("F"); // only full reg is allowed for re reg
			newRm.setRegDate(null);
			newRm.setDeRegTime(null);
			newRm.setRcReasonCode(null);
			newRm.setProvRegDate(null);
			newRm.setProvExpDate(null);
			newRm.setFirstRegDate(rm.getFirstRegDate());

			newRm.setAtfDueDate(null);
			newRm.setDetainDate(null);
			newRm.setDetainDesc(null);
			newRm.setDetainStatus(null);
			
			newRm.setRegChiName(rm.getRegChiName());
			newRm.setRegName(rm.getRegName());
			if (old.getOffNo() != null && !old.getOffNo().equals(rm.getOffNo())) {
				newRm.setOffNo(rm.getOffNo());
				newRm.setOffResvDate(now);
			}
			if (old.getCallSign() != null && !old.getCallSign().equals(rm.getCallSign())) {
				newRm.setCallSign(rm.getCallSign());
				newRm.setCsResvDate(now);
			}
			rmDao.save(newRm);

			ApplDetail applDetail = new ApplDetail();
			applDetail.setApplNo(nextApplNo);
			applDetail.setPrevName(old.getRegName());
			adDao.save(applDetail);

			// included owner, DC, RP, builder/marker
			IOwnerDao ownerDao = (IOwnerDao) getDao(org.mardep.ssrs.domain.sr.Owner.class);
			List<org.mardep.ssrs.domain.sr.Owner> owners = ownerDao.findByApplId(rm.getApplNo());
			for (org.mardep.ssrs.domain.sr.Owner owner : owners) {
				Owner newOwner = owner.clone();
				newOwner.setApplNo(nextApplNo);
				ownerDao.save(newOwner);
			}

			IRepresentativeDao rpDao = (IRepresentativeDao) getDao(Representative.class);
			Representative rp = rpDao.findById(rm.getApplNo());
			Representative newRp = rp.clone();
			newRp.setApplNo(nextApplNo);
			rpDao.save(newRp);

			IBuilderMakerDao bmDao = (IBuilderMakerDao) getDao(BuilderMaker.class);
			BuilderMaker bmCriteria = new BuilderMaker();
			bmCriteria.setApplNo(rm.getApplNo());
			List<BuilderMaker> builders = bmDao.findByCriteria(bmCriteria);
			for (BuilderMaker bm : builders) {
				BuilderMaker newBm = bm.clone();
				newBm.setApplNo(nextApplNo);
				newBm.setBuilderMakerId(null);
				bmDao.save(newBm);
			}

		}
		Task[] tasks = inbox.proceed(taskId, "accept", "");
		if (reReg) {
			tasks[0].setParam2(nextApplNo);
			ITaskDao dao = (ITaskDao) getDao(Task.class);
			tasks[0] = dao.save(tasks[0]);
			logger.info("REREG: new task created:" + tasks[0].getId());
		}
		return tasks;
	}

	@Override
	public Task[] approve(Long taskId, boolean reReg) {
		return inbox.proceed(taskId, "approve", "");
	}

	@Override
	public Task[] ready(RegMaster rm, Long taskId, boolean reReg) {
		// save de-reg status
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		RegMaster record = rmDao.findById(rm.getApplNo());
		record.setRegStatus(RegMaster.REG_STATUS_DEREGISTERED);
		record.setDeRegTime(rm.getDeRegTime());
		record.setRcReasonCode(rm.getRcReasonCode());
		record.setRcReasonType(rm.getRcReasonType());
		record.setRegistrar(rm.getRegistrar());

		rmDao.save(record);

		return inbox.proceed(taskId, "readyCrossCheckCod", "");
	}

	@Override
	public RegMaster complete(RegMaster record, Long taskId, boolean reReg, Transaction tx, Date deDate) {
		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		String applNo = record.getApplNo();
		record.setRegStatus(RegMaster.REG_STATUS_DEREGISTERED);
		record = rmDao.save(record);
		tx.setDateChange(record.getDeRegTime());
		tx.setHourChange(new SimpleDateFormat("HHmm").format(record.getDeRegTime()));
		tx = txDao.save(applNo, Transaction.CODE_DE_REG, tx);
		sr.saveHistory(applNo, tx);
		inbox.proceed(taskId, "complete", "");
		return record;
	}

	@Override
	public void withdraw(Long taskId, boolean reReg) {
		ITaskDao dao = (ITaskDao) getDao(Task.class);
		Task task = dao.findById(taskId);
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		RegMaster rm = null;
		if (task.getParam2() != null && !task.getParam2().isEmpty()) { // re-reg
			rm = rmDao.findById(task.getParam2());
		} else if (task.getParam1() != null && !task.getParam1().isEmpty()) { //de-reg
		}
		if (rm != null) {
			rm.setRegStatus(RegMaster.REG_STATUS_WITHDRAW);
			rmDao.save(rm);
		}
		inbox.proceed(taskId, "withdraw", "");
	}

	@Override
	public void completeNew(Long taskId, RegMaster entity, ApplDetail details, List<Owner> owners, Representative rep,
			List<BuilderMaker> bmList, Transaction tx, Date deRegTime) {
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		entity.setRegStatus(RegMaster.REG_STATUS_REGISTERED);
		rmDao.save(entity);
		IApplDetailDao adDao = (IApplDetailDao) getDao(ApplDetail.class);
		details.setApplNo(entity.getApplNo());
		if ("APP".equals(details.getCosInfoState())) {
			details.setCosInfoState("COS");
		}
		adDao.save(details);
		IOwnerDao owDao = (IOwnerDao) getDao(Owner.class);
		for (Owner owner : owners) {
			owDao.save(owner);
		}
		copyRp(entity, rep);
		IBuilderMakerDao bmDao = (IBuilderMakerDao) getDao(BuilderMaker.class);
		for (BuilderMaker bm : bmList) {
			bmDao.save(bm);
		}
		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
		String applNo = entity.getApplNo();

		BigDecimal calculateAtc = rmDao.calculateAtc(new String[] {applNo}).get(applNo);
		IFeeCodeDao fcDao = (IFeeCodeDao) getDao(FeeCode.class);
		FeeCode initialReg = fcDao.findById("02");

		Date generationTime = new Date();
		DemandNoteItem dni02 = new DemandNoteItem();
		dni02.setActive(true);
		// rereg, 100% initial full reg based on GT
		dni02.setAmount(rmDao.calculateRegFee(entity.getGrossTon()));
		dni02.setApplNo(applNo);
		dni02.setChargedUnits(1);
		dni02.setFcFeeCode(initialReg.getId());
		dni02.setGenerationTime(generationTime);
		demandNoteService.addItem(dni02);

		if (calculateAtc != null) {
			DemandNoteItem atc = new DemandNoteItem();
			atc.setActive(true);
			atc.setAmount(calculateAtc);
			atc.setApplNo(applNo);
			atc.setChargedUnits(1);
			atc.setFcFeeCode("01");
			atc.setGenerationTime(generationTime);
			int year = Calendar.getInstance().get(Calendar.YEAR);
			atc.setAdhocDemandNoteText("100% (Year " + year + "-" + (year + 1)+ ")");
			demandNoteService.addItem(atc);
		}

		tx = txDao.save(applNo, Transaction.CODE_REGISTRATION, tx);
		sr.saveHistory(applNo, tx);

		inbox.proceed(taskId, "completeNewApp", "");

	}

	private void copyRp(RegMaster entity, Representative rep) {
		IRepresentativeDao rpDao = (IRepresentativeDao) getDao(Representative.class);
		Representative rp = rpDao.findById(entity.getApplNo());
		if (rp == null) {
			rp = new Representative();
		}
		rp.setAddress1(rep.getAddress1());
		rp.setAddress2(rep.getAddress2());
		rp.setAddress3(rep.getAddress3());
		rp.setEmail(rep.getEmail());
		rp.setFaxNo(rep.getFaxNo());
		rp.setHkic(rep.getHkic());
		rp.setIncorpCert(rep.getIncorpCert());
		rp.setName(rep.getName());
		rp.setStatus(rep.getStatus());
		rp.setTelNo(rep.getTelNo());
		rpDao.save(rp);
	}

	@Override
	public RegMaster previewCoD(RegMaster entity) {
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		RegMaster rm = rmDao.findById(entity.getId());
		rm.setDeRegTime(entity.getDeRegTime());
		rm.setRcReasonCode(entity.getRcReasonCode());
		rm.setRcReasonType(entity.getRcReasonType());
		rm.setRegistrar(entity.getRegistrar());
		return rmDao.save(rm);
	}
}
