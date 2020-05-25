package org.mardep.ssrs.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.dao.codetable.IFeeCodeDao;
import org.mardep.ssrs.dao.codetable.IOfficeDao;
import org.mardep.ssrs.dao.codetable.IOperationTypeDao;
import org.mardep.ssrs.dao.codetable.IShipSubTypeDao;
import org.mardep.ssrs.dao.codetable.IShipTypeDao;
import org.mardep.ssrs.dao.codetable.ISystemParamDao;
import org.mardep.ssrs.dao.sr.IApplDetailDao;
import org.mardep.ssrs.dao.sr.IBuilderMakerDao;
//import org.mardep.ssrs.dao.sr.IEtoCorDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IPreReservedNameDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dao.sr.IRepresentativeDao;
import org.mardep.ssrs.dao.sr.ITransactionDao;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.codetable.SystemParam;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.sr.Amendment;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.BuilderMaker;
//import org.mardep.ssrs.domain.sr.EtoCoR;
import org.mardep.ssrs.domain.sr.Injuction;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.domain.sr.Mortgagor;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.PreReservedName;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.sr.SrEntityListener;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.pojo.trackcode.FieldName;
import org.mardep.ssrs.pojo.trackcode.Language;
import org.mardep.ssrs.pojo.trackcode.Result;
import org.mardep.ssrs.pojo.trackcode.SearchResult;
import org.mardep.ssrs.pojo.trackcode.State;
import org.mardep.ssrs.pojo.trackcode.StatusEnum;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 *TXN_CODE	TC_DESC
41	CHANGE IN RE-REGISTRATION TYPE/REGISTRATION DATE
42	EXTENSION OF PROVISIONAL REGISTRATION
43	DE-REGISTRATION OF SHIP
44	REINSTATEMENT OF DE-REGISTERED SHIP
45	AMENDMENT OF DE-REGISTERED SHIP
15	TRANSMISSION OF OWNERSHIP W/O MORTGAGE
 * @author Leo.LIANG
 *
 */
@Service
@Transactional
public class ShipRegService extends AbstractService implements IShipRegService, DisposableBean {

	@Autowired
	IInboxService inbox;

	@Autowired
	IReservationService rs;

	@Autowired
	IPreReservedNameDao nameDao;

	@Autowired
	IRegMasterDao rmDao;

	@Autowired
	ISystemParamDao systemParamDao;

	@Autowired
	IShipTypeDao stDao;

	@Autowired
	IShipSubTypeDao ssDao;

	@Autowired
	IOwnerDao ownerDao;
	@Autowired
	IRepresentativeDao rpDao;
	@Autowired
	IBuilderMakerDao bmDao;
	@Autowired
	IApplDetailDao adDao;
	@Autowired
	IOperationTypeDao otDao;

	@Autowired
	IFsqcService fsqc;

	@Autowired
	IDemandNoteService demandNoteService;

	@Autowired
	IOfficeDao officeDao;
	
	private ExecutorService es = Executors.newFixedThreadPool(1);

	private SrEntityListener listener = new SrEntityListener(){
		@Override
		public void callback(int operation, Object obj) { String applNo = null;
			if (obj instanceof RegMaster) {
				RegMaster rm = (RegMaster) obj;
				applNo = rm.getApplNo();
			} else if (obj instanceof ApplDetail) {
				ApplDetail ad = (ApplDetail) obj;
				applNo = ad.getApplNo();
			} else if (obj instanceof Owner) {
				Owner ow = (Owner) obj;
				applNo = ow.getApplNo();
			} else if (obj instanceof BuilderMaker) {
				BuilderMaker bm = (BuilderMaker) obj;
				applNo = bm.getApplNo();
			} else if (obj instanceof Representative) {
				Representative rp = (Representative) obj;
				applNo = rp.getApplNo();
			}
			if (applNo != null) {
				String applNo_ = applNo;
				es.submit(()-> {
					try {
						fsqc.send(operation, applNo_);
					} catch (JsonProcessingException e) {
						logger.warn("failed to write {}", e);
					}
				} );
			}
		}

	};
	public ShipRegService() {
		SrEntityListener.addListener(listener);
	}
	@Override
	public RegMaster create(RegMaster entity, ApplDetail details, List<Owner> owners, Representative rep) { //, List<BuilderMaker> builders) {
		if (owners.isEmpty()) {
			throw new IllegalArgumentException("Owner missing");
		}
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		IApplDetailDao adDao = (IApplDetailDao) getDao(ApplDetail.class);
		IOwnerDao ownerDao = (IOwnerDao) getDao(Owner.class);
		IRepresentativeDao repDao = (IRepresentativeDao) getDao(Representative.class);

		if (entity.getApplNo() == null) {
			String applNo = nextApplNo();
			entity.setApplNo(applNo);
			entity.setRegStatus(RegMaster.REG_STATUS_ACTIVE);
		}
		RegMaster saved = rmDao.save(entity);

		rep.setApplNo(entity.getApplNo());
		repDao.save(rep);

		details.setApplNo(entity.getApplNo());
		adDao.save(details);

		int seq = 1;
		for (Owner owner : owners) {
			owner.setApplNo(entity.getApplNo());
			owner.setOwnerSeqNo(seq++);
			ownerDao.save(owner);
		}

//		if (builders.size()>0) {
//			int code = 1;
//			for (BuilderMaker builder:builders) {
//				builder.setApplNo(entity.getApplNo());
//				builder.setBuilderCode(Integer.toString(code++));
//				builderDao.save(builder);
//			}
//		}
		String officeCode="";
		String cor=saved.getCorCollect();
		String userGroup="";
		if(cor !=null) {
//		 userGroup = userGroupDao.findById(Long.parseLong(saved.getCorCollect())).getGroupCode();
			officeCode=officeDao.findById(Integer.parseInt(cor)).getCode();
		}

//		inbox.startWorkflow(WORKFLOW_NEW_SHIP_REG, saved.getApplNo(), "", "", "");
		inbox.startWorkflow(WORKFLOW_NEW_SHIP_REG, saved.getApplNo(),officeCode, "", "");

		return saved;
	}

	@Override
	public RegMaster changeDetails(String name, String param1_applNo, String param2, String param3, String properties) {
		RegMaster regMaster= ((IRegMasterDao) getDao(RegMaster.class)).findById(param1_applNo);
		String cor=regMaster.getCorCollect();
		String officeCode="";
		if(cor !=null) {
			officeCode=officeDao.findById(Integer.parseInt(cor)).getCode();
		}
		inbox.startWorkflow(name, param1_applNo, officeCode, "", "");
		
		return regMaster;
	}
	
	@Override
	public RegMaster changeDetailsProcedure(RegMaster regMaster, Long taskId, String action) {
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		String officeCode = "";
		String cor = regMaster.getCorCollect();
		String userGroup = "";
		if (cor != null) {
			officeCode = officeDao.findById(Integer.parseInt(cor)).getCode();
		}
		inbox.proceedWithNewParam2(taskId, action, officeCode, "");
		return rmDao.save(regMaster);
	}

	@Override
	public String nextApplNo() {
		String applNo;
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
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
		return applNo;
	}

	@Override
	public RegMaster requestAccept(RegMaster regMaster, List<Owner> owners, Long taskId) {

		List<String> ownerNames = new ArrayList<>();
		for (Owner owner : owners) {
			ownerNames.add(owner.getName());
		}
		PreReservedName name = new PreReservedName();
		name.setName(regMaster.getRegName());
		name.setLanguage(PreReservedName.LANG_EN);
		isNameAvailable(ownerNames, name, regMaster.getApplNo());
		name.setName(regMaster.getRegChiName());
		name.setLanguage(PreReservedName.LANG_ZH);
		if (regMaster.getRegChiName() != null && !regMaster.getRegChiName().isEmpty()) { // this is not mandatory
			isNameAvailable(ownerNames, name, regMaster.getApplNo());
		}

		regMaster.setRegStatus(RegMaster.REG_STATUS_ACTIVE);
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		String officeCode = "";
		String cor = regMaster.getCorCollect();
		if (cor != null) {
			officeCode = officeDao.findById(Integer.parseInt(cor)).getCode();
		}
		inbox.proceedWithNewParam2(taskId, "requestAccept", officeCode, "");
		return rmDao.save(regMaster);
	}

	private void isNameAvailable(List<String> ownerNames, PreReservedName name, String applNo) {
		List<PreReservedName> names = nameDao.findNames(name.getName(), name.getLanguage());
		for (PreReservedName pr : names) {
			if (pr.getReleaseTime() != null && pr.getReleaseTime().getTime() < System.currentTimeMillis()) { // skip withdrawn reservation checking
			} else if (!ownerNames.contains(pr.getOwnerName()) && pr.getExpiryTime().getTime() > System.currentTimeMillis()) {
				// reserved by other
				Representative rp = rpDao.findByApplId(applNo);
				if (rp != null && rp.getName() != null &&
						!rp.getName().equals(pr.getApplName()) &&
						!rp.getName().equals(pr.getOwnerName())) {
					throw new IllegalArgumentException(pr.getName() + " is reserved by " +pr.getOwnerName() + " until " + pr.getExpiryTime());
				}
			}
		}
		// in use or registering?
		RegMaster rmCriteria = new RegMaster();
		if (PreReservedName.LANG_EN.equals(name.getLanguage())) {
			rmCriteria.setRegName(name.getName());
		} else {
			rmCriteria.setRegChiName(name.getName());
		}
		List<RegMaster> regs = rmDao.findByCriteria(rmCriteria);
		for (RegMaster reg : regs) {
			if (!RegMaster.REG_STATUS_DEREGISTERED.equals(reg.getRegStatus()) &&
					!RegMaster.REG_STATUS_WITHDRAW.equals(reg.getRegStatus()) &&
					!applNo.equals(reg.getApplNo())) {
				throw new IllegalArgumentException(name.getName() + " is using by " + reg.getApplNo());
			}
		}
	}

	@Override
	public RegMaster accept(RegMaster regMaster, Long taskId) {
		// TODO Assign Call Sign, Official Number, send AIP. The record will be Pending
		// for Documents
		Date now = new Date();
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		regMaster.setCsResvDate(now);
		regMaster.setOffResvDate(now);
		String officeCode = "";
		String cor = regMaster.getCorCollect();
		if (cor != null) {
			officeCode = officeDao.findById(Integer.parseInt(cor)).getCode();
		}
		inbox.proceedWithNewParam2(taskId, "accept", officeCode, "");
		return rmDao.save(regMaster);
	}

	@Override
	public RegMaster approveReady(RegMaster regMaster, Long taskId) {
		// TODO check registration/prov registration date
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		String officeCode = "";
		String cor = regMaster.getCorCollect();
		if (cor != null) {
			officeCode = officeDao.findById(Integer.parseInt(cor)).getCode();
		}
		inbox.proceedWithNewParam2(taskId, "ready", officeCode, "");
		return rmDao.save(regMaster);
	}

	//RegMasterDS_updateData_crossCheckReady_changeDetails
	@Override
	public RegMaster changeDetailsCrossCheckReady(RegMaster regMaster, Long taskId, String action) {
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		String officeCode = "";
		String cor = regMaster.getCorCollect();
		String userGroup = "";
		if (cor != null) {
			officeCode = officeDao.findById(Integer.parseInt(cor)).getCode();
		}
//		inbox.proceedWithNewParam2(taskId, "corReady", officeCode, "");
		inbox.proceedWithNewParam3(taskId, action, officeCode, "CoR Pre-Approved", "");
		return rmDao.save(regMaster);		
	}
	
	@Override
	public RegMaster corReady(RegMaster regMaster, Long taskId) {
		// TODO mark cross-checked CoR
		// TODO state to be Pending for PDA or Equivalent
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		String officeCode = "";
		String cor = regMaster.getCorCollect();
		String userGroup = "";
		if (cor != null) {
			officeCode = officeDao.findById(Integer.parseInt(cor)).getCode();
		}
//		inbox.proceedWithNewParam2(taskId, "corReady", officeCode, "");
		inbox.proceedWithNewParam3(taskId, "corReady", officeCode, "CoR Pre-Approved", "");
		return rmDao.save(regMaster);
	}

	@Override
	public RegMaster approve(RegMaster regMaster, Long taskId) {
		String imoNo = regMaster.getImoNo();
		if (imoNo != null) {
			RegMaster criteria = new RegMaster();
			criteria.setImoNo(imoNo);
			criteria.setRegStatus("R");
			List<RegMaster> list = findByCriteria(criteria);
			if (!list.isEmpty()) {
				throw new RuntimeException("IMO number is used");
			}
		}
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		String officeCode = "";
		String cor = regMaster.getCorCollect();
		String userGroup = "";
		if (cor != null) {
			officeCode = officeDao.findById(Integer.parseInt(cor)).getCode();
		}
		inbox.proceedWithNewParam2(taskId, "approve", officeCode, "");
		return rmDao.save(regMaster);
	}

	@Override
	public RegMaster complete(RegMaster regMaster, Long taskId) {
		// TODO Email Memo to CO/SD
		// TODO Print demand note state to be Application Completed
		// 20190918 comment this line so that Pro-Reg can proceed
		//regMaster.setApplNoSuf(regMaster.getRegDate() != null ? "F" : "P");
		Date regdate = regMaster.getRegDate();
		if (regdate == null) {
			regdate = new Date();
		}
		regMaster.setRegStatus(RegMaster.REG_STATUS_REGISTERED);
		BigDecimal calculateAtc = rmDao.calculateAtc(new String[] {regMaster.getApplNo()}).get(regMaster.getApplNo());
		IFeeCodeDao fcDao = (IFeeCodeDao) getDao(FeeCode.class);
		if ("F".equals(regMaster.getApplNoSuf())) {
			if (regMaster.getRegDate() != null && regMaster.getFirstRegDate() == null) {
				regMaster.setFirstRegDate(regMaster.getRegDate());
			}
			// full reg fee item
			FeeCode initialReg = fcDao.findById("02");

			Date generationTime = new Date();
			DemandNoteItem entity = new DemandNoteItem();
			entity.setActive(true);
			// full, 100% initial full reg based on GT
			entity.setAmount(rmDao.calculateRegFee(regMaster.getGrossTon()));
			entity.setApplNo(regMaster.getApplNo());
			entity.setChargedUnits(1);
			entity.setFcFeeCode(initialReg.getId());
			entity.setGenerationTime(generationTime);
			demandNoteService.addItem(entity);

			if (calculateAtc != null) {
				DemandNoteItem atc = new DemandNoteItem();
				atc.setActive(true);
				atc.setAmount(calculateAtc);
				atc.setApplNo(regMaster.getApplNo());
				atc.setChargedUnits(1);
				atc.setFcFeeCode("01");
				atc.setGenerationTime(generationTime);
				int year = Calendar.getInstance().get(Calendar.YEAR);
				atc.setAdhocDemandNoteText("100% (Year " + year + "-" + (year + 1)+ ")");
				demandNoteService.addItem(atc);
			}
		} else {
			if (regMaster.getProvRegDate() != null) {
				regdate = regMaster.getProvRegDate();
			}
			// default pro exp date
			Calendar instance = Calendar.getInstance();
			instance.setTime(regdate);
			instance.add(Calendar.MONTH, 1);
			regMaster.setProvExpDate(instance.getTime());
//			Fee code 05 - Provisional Registration
//			Fee code 07 - 8.33% Annual Tonnage Charge (Provisional Registration)
			FeeCode fc05 = fcDao.findById("05");

			Date generationTime = new Date();
			DemandNoteItem entity = new DemandNoteItem();
			entity.setActive(true);
			// proreg, 35% initial full reg based on GT
			entity.setAmount(new BigDecimal("0.35").multiply(rmDao.calculateRegFee(regMaster.getGrossTon())));
			entity.setApplNo(regMaster.getApplNo());
			entity.setChargedUnits(1);
			entity.setFcFeeCode(fc05.getId());
			entity.setGenerationTime(generationTime);
			demandNoteService.addItem(entity);

			if (calculateAtc != null) {
				DemandNoteItem atc = new DemandNoteItem();
				atc.setActive(true);
				atc.setAmount(calculateAtc.multiply(new BigDecimal("0.0833")));
				atc.setApplNo(regMaster.getApplNo());
				atc.setChargedUnits(1);
				atc.setFcFeeCode("07");
				atc.setGenerationTime(generationTime);
				demandNoteService.addItem(atc);
			}
		}

		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		String officeCode = "";
		String cor = regMaster.getCorCollect();
		String userGroup = "";
		if (cor != null) {
			officeCode = officeDao.findById(Integer.parseInt(cor)).getCode();
		}
		inbox.proceedWithNewParam2(taskId, "complete", officeCode, "");
		RegMaster result = rmDao.save(regMaster);
		ApplDetail ad = adDao.findById(regMaster.getId());
		if (ad != null && "APP".equals(ad.getCosInfoState())) {
			ad.setCosInfoState("COS");
			adDao.save(ad);
		}
		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
		Transaction tx = new Transaction();
		tx.setApplNo(result.getApplNo());
		tx.setCode(Transaction.CODE_REGISTRATION);
		tx.setDateChange(regdate);
		tx.setDetails("");
		SimpleDateFormat format = new SimpleDateFormat("HHmm");
		tx.setHourChange(format.format(tx.getDateChange()));
		tx.setRegMaster(result);
		tx = txDao.save(result.getApplNo(), tx.getCode(), tx);
		saveHistory(result.getApplNo(), tx);
		return result;
	}

	@Override
	public RegMaster check(RegMaster regMaster, List<Owner> owners) {
		// TODO check IMO
		String name = regMaster.getRegName();
		RegMaster result = new RegMaster();

		if (name != null) {
			Map<String, String[]> enResult = rs.check(Arrays.asList(name), true);
			if (!enResult.isEmpty()) {
				String[] messages = enResult.values().iterator().next();
				if (IReservationService.RESULT_REGISTERED.equals(messages[0]) && messages[1].equals(regMaster.getApplNo())) {
					result.setRegName(null);
				} else if (IReservationService.RESULT_RESERVED.equals(messages[0]) && owners.removeIf(owner->{ return owner.getName() != null && owner.getName().equals(messages[3]);})) {
					result.setRegName(null);
				} else {
					for (int i = 1; i < messages.length; i++) {
						messages[0] += "|" + messages[i];
					}
					result.setRegName(messages[0]);
				}
			} else {
				result.setRegName(null);
			}
		}

		String regChiName = regMaster.getRegChiName();
		if (regChiName != null) {
			Map<String, String[]> chResult = rs.check(Arrays.asList(regChiName), false);
			if (!chResult.isEmpty()) {
				String[] messages = chResult.values().iterator().next();
				if (IReservationService.RESULT_REGISTERED.equals(messages[0]) && messages[1].equals(regMaster.getApplNo())) {
					result.setRegChiName(null);
				} else if (IReservationService.RESULT_RESERVED.equals(messages[0]) && owners.removeIf(owner->{ return owner.getName() != null && owner.getName().equals(messages[3]);})) {
					result.setRegChiName(null);
				} else {
					for (int i = 1; i < messages.length; i++) {
						messages[0] += "|" + messages[i];
					}
					result.setRegChiName(messages[0]);
				}
			} else {
				result.setRegChiName(null);
			}
		}

		return result;
	}

	@Override
	public String getCallSign() {
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		String callSign = rmDao.nextCallSign();
		return callSign;
	}

	@Override
	public String getOffNo() {
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		String maxOffNo = rmDao.maxOffNo();
		if (maxOffNo == null) {
			maxOffNo = "HK-0000";
		}
		if (maxOffNo.length() > 6) {
			return "HK-" + String.valueOf(Integer.parseInt(maxOffNo.substring(3)) + 10001).substring(1);
		} else {
			return "HK-0001";
		}
	}

	@Override
	public RegMaster withdraw(RegMaster entity, Long taskId, boolean byApplicant) {
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		entity.setRegStatus(RegMaster.REG_STATUS_WITHDRAW);
		inbox.proceed(taskId, byApplicant ? "withdraw" : "reject", "");
		return rmDao.save(entity);
	}

	@Override
	public RegMaster reset(RegMaster entity, Long taskId) {
		IRegMasterDao rmDao = (IRegMasterDao) getDao(RegMaster.class);
		entity.setRegStatus(null);
		inbox.proceed(taskId, "reset", "");
		return rmDao.save(entity);
	}

	@Override
	public List<Owner> createOwners(List<Owner> owners) {
		ArrayList<String> applList = new ArrayList<String>();
		owners.forEach(owner -> {
			if (owner.getApplNo() != null) {
				applList.add(owner.getApplNo());
				if (owner.getOwnerSeqNo() == null) {
					int max = 0;
					for (Owner own: owners) {
						if (own.getOwnerSeqNo() != null && own.getOwnerSeqNo() > max){
							max = own.getOwnerSeqNo();
						}
						owner.setOwnerSeqNo(max + 1);
					}
				}
			}
		});
		IOwnerDao ownerDao = (IOwnerDao) getDao(Owner.class);
		ArrayList<Owner> result = new ArrayList<Owner>();
		owners.forEach(owner -> {
			result.add(ownerDao.save(owner));
		});

		return result;
	}
	@Override
	public Owner amendOwner(Owner entity, Amendment amm) {
		IOwnerDao ownerDao = (IOwnerDao) getDao(Owner.class);
		entity = ownerDao.save(entity);
		saveHistory(entity.getApplNo(), amm);
		return entity;
	}

	@Override
	public Owner updateOwner(Owner entity, Transaction tx) {
		IOwnerDao ownerDao = (IOwnerDao) getDao(Owner.class);

		// added handling of no sequence no. for case of transfer/transmit ownership
		if (tx == null) {
			ownerDao.save(entity);
		} else {
			String txCode = tx.getCode();
			if (entity.getOwnerSeqNo()!=null) {
				Owner criteria = new Owner();
				criteria.setApplNo(entity.getApplNo());
				criteria.setOwnerSeqNo(entity.getOwnerSeqNo());
				List<Owner> owners = ownerDao.findByCriteria(criteria);
				if (owners.size()==0) {
				} else if (owners.size() == 1) {
					if (txCode == null) {
						txCode = Transaction.CODE_CHG_OWNER_OTHERS;
						if (!same(owners.get(0).getName(), entity.getName())) {
							txCode = Transaction.CODE_CHG_OWNER_NAME;
						} else if (!same(owners.get(0).getAddress1(), entity.getAddress1()) ||
								!same(owners.get(0).getAddress2(), entity.getAddress2()) ||
								!same(owners.get(0).getAddress3(), entity.getAddress3()) ){
							txCode = Transaction.CODE_CHG_OWNER_ADDR;
						}
					}
					entity = ownerDao.save(entity);
				} else {
					throw new IllegalStateException("more than 1 owner found by criteria" + entity);
				}
			}
			tx = ((ITransactionDao) getDao(Transaction.class)).save(entity.getApplNo(), txCode, tx);
			saveHistory(entity.getApplNo(), tx);
		}
		return entity;
	}

	private boolean same(Object a, Object b) {
		return a == null ? b == null : a.equals(b);
	}

	@Override
	public void receiveOwnerChange(String applNo, int ownerSeq) {
		inbox.startWorkflow("ownerChange", applNo, String.valueOf(ownerSeq), "", "");
	}
	@Override
	public void acceptOwnerChange(Long taskId) {
		inbox.proceed(taskId, "acceptOwnerChange", "");
	}
	@Override
	public void approveOwnerChange(Long taskId) {
		inbox.proceed(taskId, "approveOwnerChange", "");
	}
	@Override
	public void crosscheckOwnerChange(Long taskId) {
		inbox.proceed(taskId, "readyCrossCheck", "");
	}
	@Override
	public Owner completeOwnerChange(Owner entity, Long taskId, Transaction tx) {
		Owner owner = updateOwner(entity, tx);
		inbox.proceed(taskId, "completeOwnerChange", "");
		return owner;
	}
	@Override
	public void withdrawOwnerChange(Long taskId) {
		inbox.proceed(taskId, "withdrawOwnerChange", "");
	}

	@Override
	public void removeBuilder(BuilderMaker entity) {
		IBuilderMakerDao dao = (IBuilderMakerDao) getDao(BuilderMaker.class);
		BuilderMaker bm = dao.findById(entity.getId());
		dao.delete(bm);
	}

	@Override
	public List<Owner> findOwners(String applNo, Date reportDate) {
		return ((IOwnerDao) getDao(Owner.class)).findHistory(applNo, reportDate);
	}

	@Override
	public Representative findRepById(String applNo, Date reportDate) {
		return ((IRepresentativeDao) getDao(Representative.class)).findByApplId(applNo, reportDate);
	}

	@Override
	public Representative findRpByApplId(String applNo) {
		return ((IRepresentativeDao) getDao(Representative.class)).findByApplId(applNo);
	}

	@Override
	public RegMaster findById(String applNo, Date reportDate) {
		return ((IRegMasterDao) getDao(RegMaster.class)).findByApplId(applNo, reportDate);
	}
	@Override
	public List<BuilderMaker> findBuilders(String applNo, Date reportDate) {
		return ((IBuilderMakerDao) getDao(BuilderMaker.class)).findHistory(applNo, reportDate);
	}

	@Override
	public List<BuilderMaker> findBuildersByApplId(String applNo){
		return ((IBuilderMakerDao) getDao(BuilderMaker.class)).findByApplId(applNo);
	}

	@Override
	public List<Owner> findOwnersByApplId(String applNo) {
		return ((IOwnerDao) getDao(Owner.class)).findByApplId(applNo);
	}

	@Override
	public List<Owner> findOwnersByName(String name) {
		Owner owner = new Owner();
		owner.setName(name);
		List<Owner> owners = getDao(Owner.class).findByCriteria(owner);
		return owners;
	}

	@Override
	public List<RegMaster> findByApplNoList(List list) {
		return ((IRegMasterDao) getDao(RegMaster.class)).findByApplNoList(list);
	}

	@Override
	public String prepareTrackCode(String applNo) {
		SystemParam param = systemParamDao.findById("SSRS_LAST_TRACK_CODE");
		String value = param.getValue();
		int last = Integer.parseInt(value);
		String next = String.valueOf(last + 1000001);
		RegMaster rm = rmDao.findById(applNo);
		String offNo = rm.getOffNo();
		if (offNo == null) {
			offNo = "0000000";
		} else if (offNo.length() < 7) {
			offNo += "0000000";
		}
		String trackCode = "00" + offNo.substring(3, 6) + "-" + offNo.substring(6, 7) + Calendar.getInstance().get(Calendar.YEAR) +
				"-" + next.substring(next.length() - 5);
		
		param.setValue(String.valueOf(last + 1));
		systemParamDao.save(param);

		return trackCode;
	}
	
	@Override
	public RegMaster assignRegDateTrackCode(String applNo, String applNoSuf, Date regDate, String trackCode) {
		RegMaster rm = rmDao.findById(applNo);
		
		rm.setApplNoSuf(applNoSuf);
		rm.setRegDate(regDate);

		if ("P".equals(applNoSuf)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(regDate);
			cal.add(Calendar.MONTH, 1);
			rm.setProvExpDate(cal.getTime());
		} else if ("F".equals(applNoSuf)) {
			rm.setProvExpDate(null);
		}
		rm.setTrackCode(trackCode);
		RegMaster result = rmDao.save(rm);
		
		Transaction tx = new Transaction();
		tx.setApplNo(result.getApplNo());
		tx.setCode("72");
		if ("A".equals(rm.getRegStatus())) {
			tx.setDateChange(rm.getRegDate());
		} else {
			tx.setDateChange(new Date());
		}
		tx.setDetails("");
		SimpleDateFormat format = new SimpleDateFormat("HHmm");
		tx.setHourChange(format.format(tx.getDateChange()));
		tx.setRegMaster(result);
		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
		tx = txDao.save(result.getApplNo(), tx.getCode(), tx);
		saveHistory(applNo, tx);

		return rm;
	}
	
	@Override
	public RegMaster assignTrackCode(String applNo, String trackCode) {
		RegMaster rm = rmDao.findById(applNo);
		
		rm.setTrackCode(trackCode);
		RegMaster result = rmDao.save(rm);
		Transaction tx = new Transaction();
		tx.setApplNo(result.getApplNo());
		tx.setCode("72");
		if ("A".equals(rm.getRegStatus())) {
			tx.setDateChange(rm.getRegDate());
		} else {
			tx.setDateChange(new Date());
		}
		tx.setDetails("");
		SimpleDateFormat format = new SimpleDateFormat("HHmm");
		tx.setHourChange(format.format(tx.getDateChange()));
		tx.setRegMaster(result);
		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
		tx = txDao.save(result.getApplNo(), tx.getCode(), tx);
		saveHistory(applNo, tx);
		
		return rm;
	}
	
	@Override
	public RegMaster updateTrackCode(String applNo) {
//		SystemParam param = systemParamDao.findById("SSRS_LAST_TRACK_CODE");
//		String value = param.getValue();
//		int last = Integer.parseInt(value);
//		String next = String.valueOf(last + 1000001);
//		RegMaster rm = rmDao.findById(applNo);
//		String offNo = rm.getOffNo();
//		if (offNo == null) {
//			offNo = "0000000";
//		} else if (offNo.length() < 7) {
//			offNo += "0000000";
//		}
//		String trackCode = "00" + offNo.substring(3, 6) + "-" + offNo.substring(6, 7) + Calendar.getInstance().get(Calendar.YEAR) +
//				"-" + next.substring(next.length() - 5);
		String trackCode = prepareTrackCode(applNo);
		RegMaster rm = assignTrackCode(applNo, trackCode);
//		RegMaster rm = rmDao.findById(applNo);
//		
//		rm.setTrackCode(trackCode);
//		RegMaster result = rmDao.save(rm);
//		Transaction tx = new Transaction();
//		tx.setApplNo(result.getApplNo());
//		tx.setCode("72");
//		if ("A".equals(rm.getRegStatus())) {
//			tx.setDateChange(rm.getRegDate());
//		} else {
//			tx.setDateChange(new Date());
//		}
//		tx.setDetails("");
//		SimpleDateFormat format = new SimpleDateFormat("HHmm");
//		tx.setHourChange(format.format(tx.getDateChange()));
//		tx.setRegMaster(result);
//		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
//		tx = txDao.save(result.getApplNo(), tx.getCode(), tx);
//		saveHistory(applNo, tx);
////		param.setValue(String.valueOf(last + 1));
////		systemParamDao.save(param);
		return rm;
	}
	@Override
	public RegMaster amendParticulars(RegMaster entity, Amendment amm) {
		RegMaster saved = rmDao.save(entity);
		saveHistory(entity.getApplNo(), amm);
		return saved;
	}
	@Override
	public RegMaster completeChangeDetails(RegMaster entity, Long taskId, Transaction tx) {
		RegMaster saved = rmDao.save(entity);
		tx.setCode(Transaction.CODE_CHG_SHIP_PARTICULARS);
		if ("F".equals(saved.getApplNoSuf())) {
			List<RegMaster> history = rmDao.findHistory(entity.getApplNo(), new Date());
			if (!history.isEmpty()) {
				if (!"F".equals(history.get(0).getApplNoSuf())) {
					// P to F
					if (saved.getFirstRegDate() == null) {
						saved.setFirstRegDate(saved.getRegDate());
					}
					tx.setCode(Transaction.CODE_REGISTRATION);
					tx.setDateChange(saved.getRegDate());
					// p to f fee item
//					Fee code 06 - Change from Provisional to Full Registration
//					Fee code 39 - 100% Annual Tonnage Charge (Change from Provisional to Full Registration)
					IFeeCodeDao fcDao = (IFeeCodeDao) getDao(FeeCode.class);
					FeeCode fc06 = fcDao.findById("06");

					Date generationTime = new Date();
					DemandNoteItem p2fFee = new DemandNoteItem();
					p2fFee.setActive(true);
					// p to f, 75% initial full reg based on GT
					p2fFee.setAmount(new BigDecimal("0.75").multiply(rmDao.calculateRegFee(saved.getGrossTon())));
					p2fFee.setApplNo(entity.getApplNo());
					p2fFee.setChargedUnits(1);
					p2fFee.setFcFeeCode(fc06.getId());
					p2fFee.setGenerationTime(generationTime);
					demandNoteService.addItem(p2fFee);

					BigDecimal calculateAtc = rmDao.calculateAtc(new String[] {entity.getApplNo()}).get(entity.getApplNo());
					if (calculateAtc != null) {
						DemandNoteItem atc = new DemandNoteItem();
						atc.setActive(true);
						atc.setAmount(calculateAtc);
						atc.setApplNo(entity.getApplNo());
						atc.setChargedUnits(1);
						atc.setFcFeeCode("39");
						atc.setGenerationTime(generationTime);
						demandNoteService.addItem(atc);
					}

				}
			}
		}
		tx = ((ITransactionDao) getDao(Transaction.class)).save(entity.getApplNo(), tx.getCode(), tx);
		saveHistory(saved.getApplNo(), tx);

		inbox.proceed(taskId, "complete", "");
		return saved;
	}

	@Override
	public Result check(String trackCode, Language language){
//		TODO change to DB later
		Result result = null;
		try {
			RegMaster rm = rmDao.checkTrackCode(trackCode);
			if (rm == null) {
				result = new Result(new State(trackCode, StatusEnum.NOTRETRIEVED, language));
			} else if (RegMaster.REG_STATUS_DEREGISTERED.equals(rm.getRegStatus())) {
				result = new Result(new State(trackCode, StatusEnum.DEREGISTERED, language));
			} else {
				SearchResult searchResult = new SearchResult(language);
				searchResult.add(FieldName.NAME_OF_SHIP,
						rm.getRegName() +
						(rm.getRegChiName() == null ? "" : (" "+rm.getRegChiName())));
				searchResult.add(FieldName.IMO_NO, rm.getImoNo());
				Date regDate = rm.getRegDate();
				if (regDate != null) {
					searchResult.add(FieldName.DATE_OF_REGISTRY, new SimpleDateFormat("dd/MM/yyyy").format(regDate));
				}
				searchResult.add(FieldName.TYPE_OF_SHIP, rm.getSurveyShipType());
				BigDecimal gt = rm.getGrossTon();
				DecimalFormat decimalFormat = new DecimalFormat("#.###");
				if (gt!= null) {
					searchResult.add(FieldName.GROSS_TONNAGE, decimalFormat.format(gt));
				}
				BigDecimal nt = rm.getRegNetTon();
				if (nt != null) {
					searchResult.add(FieldName.NET_TONNAGE, decimalFormat.format(nt));
				}
				result = new Result(searchResult);
			}
		} catch (IllegalStateException e) {
			result = new Result(new State(trackCode, StatusEnum.EXPIRED, language));
		}
		return result;
	}
	@Override
	public void receiveRpChange(String applNo) {
		inbox.startWorkflow("rpChange", applNo, "", "", "");
	}

	@Override
	public void acceptRpChange(Long taskId) {
		inbox.proceed(taskId, "accept", "");
	}
	@Override
	public void approveRpChange(Long taskId) {
		inbox.proceed(taskId, "approve", "");
	}
	@Override
	public void crosscheckRpChange(Long taskId) {
		inbox.proceed(taskId, "readyCrossCheck", "");
	}
	@Override
	public Representative completeRpChange(Representative rp, Long taskId, Transaction tx) {
		String applNo = rp.getApplNo();
		IRepresentativeDao rpDao = (IRepresentativeDao) getDao(Representative.class);
		Representative existing = rpDao.findById(applNo);
		String code = Transaction.CODE_CHG_RP_OTHERS;
		if (existing != null) {
			if (existing.getName() == null && rp.getName() != null) {
				code = Transaction.CODE_CHG_RP_NAME;
			} else if (existing.getName() != null && !existing.getName().equals(rp.getName())) {
				code = Transaction.CODE_CHG_RP_NAME;
			} else {
				String exAddr = existing.getAddress1() + existing.getAddress2() + existing.getAddress3();
				String newAddr = rp.getAddress1() + rp.getAddress2() + rp.getAddress3();
				if (!exAddr.equals(newAddr)) {
					code = Transaction.CODE_CHG_RP_ADDR;
				}
			}
		}
		Representative result = rpDao.save(rp);
		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
		tx = txDao.save(applNo, code, tx); // TODO fix code
		saveHistory(applNo, tx);
		inbox.proceed(taskId, "complete", "");
		return result;
	}
	@Override
	public void withdrawRpChange(Long taskId) {
		inbox.proceed(taskId, "withdraw", "");
	}

//	@Override
//	public Representative amendRP(Representative entity, Amendment amm) {
//		IRepresentativeDao rpDao = (IRepresentativeDao) getDao(Representative.class);
//		entity = rpDao.save(entity);
//		saveHistory(entity.getApplNo(), amm);
//		return entity;
//	}
//
	@Override
	public void receiveBuilderMakerChange(String applNo, String builderCode) {
		inbox.startWorkflow("bmChange", applNo, builderCode, "", "");
	}
	@Override
	public void acceptBuilderMakerChange(Long taskId) {
		inbox.proceed(taskId, "accept", "");
	}
	@Override
	public void approveBuilderMakerChange(Long taskId) {
		inbox.proceed(taskId, "approve", "");
	}
	@Override
	public void crosscheckReadyBuilderMakerChange(Long taskId) {
		inbox.proceed(taskId, "readyCrossCheck", "");
	}
	@Override
	public BuilderMaker completeBuilderMakerChange(BuilderMaker entity, Long taskId, Transaction tx) {
		IBuilderMakerDao bmDao = (IBuilderMakerDao) getDao(BuilderMaker.class);
		inbox.proceed(taskId, "complete", "");
		ITransactionDao txDao = (ITransactionDao) getDao(Transaction.class);
		tx = txDao.save(null, Transaction.CODE_BUILDER_DETAILS, tx);
		entity = bmDao.save(entity);
		saveHistory(entity.getApplNo(), tx);
		return entity;
	}
	@Override
	public BuilderMaker withdraw(BuilderMaker entity, Long taskId) {
		inbox.proceed(taskId, "withdraw", "");
		return entity;
	}
	@Override
	public BuilderMaker updateBuilderMaker(BuilderMaker entity) {
		BuilderMaker savedEntity = bmDao.update(entity);
		return savedEntity;
	}
	@Override
	public void saveHistory(String applNo, Amendment amm) {
		getDao(Amendment.class).save(amm);
		List resultList = new ArrayList<>();
		PagingCriteria pagingCriteria = new PagingCriteria(0, 1);
		SortByCriteria sortByCriteria = new SortByCriteria(Arrays.asList("-id"));
		Map<String, Criteria> map = new HashMap<>();
		map.put("applNo", new Criteria("applNo", applNo));
		getDao(Transaction.class).findByPaging(map, sortByCriteria, pagingCriteria, resultList);
		if (resultList.isEmpty()) {
			return;
		} else {
			Transaction tx = (Transaction) resultList.get(0);
			getDao(BuilderMaker.class).deleteHistory(applNo, tx);
			getDao(Injuction.class).deleteHistory(applNo, tx);
			getDao(Mortgagee.class).deleteHistory(applNo, tx);
			getDao(Mortgage.class).deleteHistory(applNo, tx);
			getDao(Mortgagor.class).deleteHistory(applNo, tx);
			getDao(Owner.class).deleteHistory(applNo, tx);
			getDao(RegMaster.class).deleteHistory(applNo, tx);
			getDao(Representative.class).deleteHistory(applNo, tx);

			saveHistory(applNo, tx);
		}
	}

	@Override
	public void saveHistory(String applNo, Transaction tx) {
		if (tx.getVersion() == null) {
			throw new IllegalArgumentException("tx is not valid");
		}
//			//		BUILDER_MAKER_HISTS
		getDao(BuilderMaker.class).saveHistory(applNo, tx);
//			//		INJUNCTION_HISTS
		getDao(Injuction.class).saveHistory(applNo, tx);
//			//		MORTGAGEE_HISTS
		getDao(Mortgagee.class).saveHistory(applNo, tx);
//			//		MORTGAGE_HISTS
		getDao(Mortgage.class).saveHistory(applNo, tx);
//			//		MORTGAGOR_HISTS
		getDao(Mortgagor.class).saveHistory(applNo, tx);
//			//		OWNER_HISTS
		getDao(Owner.class).saveHistory(applNo, tx);
//			//		REG_MASTER_HISTS
		getDao(RegMaster.class).saveHistory(applNo, tx);
//			//		REPRESENTATIVE_HISTS
		getDao(Representative.class).saveHistory(applNo, tx);
	}
	@Override
	public void receiveTransfer(String applNo, int ownerSeq) {
		inbox.startWorkflow("transferOwnerChange", applNo, String.valueOf(ownerSeq), "", "");
	}
	@Override
	public void acceptTransfer(Long taskId) {
		inbox.proceed(taskId, "accept", "");
	}
	@Override
	public void approveTransfer(Long taskId) {
		inbox.proceed(taskId, "approve", "");
	}
	@Override
	public void crosscheckTransfer(Long taskId) {
		inbox.proceed(taskId, "readyCrossCheck", "");
	}
	@Override
	public Owner completeTransfer(Owner entity, Long taskId, Transaction tx) {
		tx.setCode(Transaction.CODE_TRANSMISSION_OWNERSHIP);
		Owner owner = updateOwner(entity, tx);
		inbox.proceed(taskId, "complete", "");
		return owner;
	}
	@Override
	public void withdrawTransfer(Long taskId) {
		inbox.proceed(taskId, "withdraw", "");
	}

	@Override
	public void destroy() throws Exception {
		es.shutdown();
	}
	
	@Override
	public int removeOwnerByApplNoAndSeq(String applNo, int seq) {
		// TODO Auto-generated method stub
		int result = ownerDao.deleteByApplNoAndSeq(applNo, seq);
		return result;
	}
	
	@Override
	public RegMaster reviseRegDateTimeAndProvExpiryDate(String applNo, Date regDate) {
		RegMaster entity = rmDao.findById(applNo);
		if (entity!=null) {
			if ("P".equals(entity.getApplNoSuf())) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(regDate);
				cal.add(Calendar.MONTH, 1);
				entity.setProvExpDate(cal.getTime());
			}
			entity.setRegDate(regDate);
			rmDao.save(entity);
		}
		return entity;
	}
	
}
