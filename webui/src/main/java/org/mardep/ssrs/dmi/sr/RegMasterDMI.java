package org.mardep.ssrs.dmi.sr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.certService.ICertIssueLogService;
import org.mardep.ssrs.dao.cert.ICertIssueLogDao;
import org.mardep.ssrs.dao.codetable.IClassSocietyDao;
import org.mardep.ssrs.dao.sr.IApplDetailDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.codetable.ClassSociety;
import org.mardep.ssrs.domain.constant.CertificateTypeEnum;
import org.mardep.ssrs.domain.entity.cert.EntityCertIssueLog;
import org.mardep.ssrs.domain.sr.Amendment;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.BuilderMaker;
//import org.mardep.ssrs.domain.sr.EtoCoR;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IDeRegService;
import org.mardep.ssrs.service.IInboxService;
import org.mardep.ssrs.service.IShipRegService;
import org.mardep.ssrs.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class RegMasterDMI extends AbstractSrDMI<RegMaster> {

	@Autowired
	IDeRegService deRegService;

	@Autowired
	MailService mailService;

	@Autowired
	IClassSocietyDao classSocDao;

	@Autowired
	IRegMasterDao regMasterDao;

	@Autowired
	IInboxService inbox;

	@Autowired
	ICertIssueLogService certIssueLogService;
	
	@Autowired
	IApplDetailDao applDetailDao;
	
	@Override
	public DSResponse fetch(RegMaster entity, DSRequest dsRequest){
		IShipRegService srService = (IShipRegService) getBaseService();
		Map clientSuppliedCriteria = dsRequest.getClientSuppliedCriteria();
		if ("AdvancedCriteria".equals(clientSuppliedCriteria.get("_constructor"))) {
			if ("and".equals(clientSuppliedCriteria.get("operator"))) {
				List<Map> criteria = (List) clientSuppliedCriteria.get("criteria");
				for (Map map : criteria) {
					if ("applNo".equals(map.get("fieldName")) &&
							"inSet".equals(map.get("operator"))) {
						List list = (List) map.get("value");
						List<RegMaster> result = srService.findByApplNoList(list);
						return new DSResponse(result, DSResponse.STATUS_SUCCESS);
					}
				}
			}
		}
		String operationId = dsRequest.getOperationId();
		if ("RegMasterDS_fetchData_checkNames".equals(operationId)) {
			Map<?,?> values = dsRequest.getClientSuppliedValues();
			List<Owner> owners = toOwners(values);
			entity = srService.check(entity, owners);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_fetchData_callSign".equals(operationId)) {
			String callSign = srService.getCallSign();
			entity.setCallSign(callSign);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_fetchData_offNo".equals(operationId)) {
			String offNo = srService.getOffNo();
			entity.setOffNo(offNo);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("FETCH_FOR_CSR".equals(operationId) && entity.getImoNo()!=null) {
			String imoNo = entity.getImoNo();
			RegMaster findForCsr = regMasterDao.findForCsr(imoNo);
			DSResponse dsResponse = new DSResponse(entity, DSResponse.STATUS_SUCCESS);
			List<RegMaster> arrayList = new ArrayList<>();
			if(findForCsr!=null){
				arrayList.add(findForCsr);
				dsResponse.setTotalRows(1);
			}
			dsResponse.setData(arrayList);
			return dsResponse;
		} else {
			return super.fetch(entity, dsRequest);
		}
	}

	@Override
	public DSResponse update(RegMaster entity, DSRequest dsRequest) throws Exception {
		IShipRegService srService = (IShipRegService) getBaseService();
		String operationId = dsRequest.getOperationId();
		Map clientValues = dsRequest.getClientSuppliedValues();
		Long taskId = (Long) clientValues.get("taskId");
		switch (operationId) {
		case "previewCoD":
		{
			return new DSResponse(deRegService.previewCoD(entity), DSResponse.STATUS_SUCCESS);
		}
		case "deReg":
			deRegService.receive(entity.getApplNo(), false);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "reReg":
			deRegService.receive(entity.getApplNo(), true);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "changeReg":
			//inbox.startWorkflow("changeDetails", entity.getApplNo(), "", "", "");
			RegMaster result = srService.changeDetails("changeDetails", entity.getApplNo(), "", "", "");
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "RegMasterDS_updateData_accept_changeDetails": {
			//inbox.proceed(taskId, "accept", "");
			entity = srService.changeDetailsProcedure(entity, taskId,"accept");												  
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		case "RegMasterDS_updateData_approve_changeDetails": {
			//inbox.proceed(taskId, "approve", "");
			entity = srService.changeDetailsProcedure(entity, taskId,"approve");														   
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		case "RegMasterDS_updateData_crossCheckReady_changeDetails": {
			//inbox.proceed(taskId, "readyCrossCheckCod", "");
			//entity = srService.changeDetailsProcedure(entity, taskId,"readyCrossCheckCod");
			entity = srService.changeDetailsCrossCheckReady(entity, taskId,"readyCrossCheckCod");
			return super.update(entity, dsRequest);
		}
		case "RegMasterDS_updateData_complete_changeDetails": {
			entity = srService.completeChangeDetails(entity, taskId, getTx(clientValues));
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		case "RegMasterDS_updateData_amend_particulars": {
			Transaction tx = getTx(clientValues);
			Amendment amm = new Amendment();
			amm.setApplNo(entity.getApplNo());
			amm.setCode(Transaction.CODE_CHG_SHIP_PARTICULARS);
			amm.setDetails(tx.getDetails());
			amm.setTransactionTime(new Date());
			amm.setUserId(UserContextThreadLocalHolder.getCurrentUserId());
			entity = srService.amendParticulars(entity, amm);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		case "RegMasterDS_updateData_withdraw_changeDetails":{
			//inbox.proceed(taskId, "withdraw", "");
			entity = srService.changeDetailsProcedure(entity, taskId,"withdraw");															
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		case "updateTrackCode":
			//Map suppliedValues = dsRequest.getClientSuppliedValues();
			//if (suppliedValues.containsKey("applNo")) {
			//	entity = srService.updateTrackCode(suppliedValues.get("applNo").toString());
			//}
			entity = srService.updateTrackCode(entity.getApplNo());
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		if ("RegMasterDS_updateData_new".equals(operationId)) {
			ApplDetail details = toApplDetails(clientValues);
			List<Owner> owners = toOwners(clientValues);
			Representative rep = toRep(clientValues);

			RegMaster result = srService.create(entity, details, owners, rep);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_requestAccept".equals(operationId)) {
			Map<?,?> values = clientValues;
			List<Owner> owners = toOwners(values);
			RegMaster result = srService.requestAccept(entity, owners, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_accept".equals(operationId)) {
			RegMaster result = srService.accept(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_approveReady".equals(operationId)) {
			RegMaster result = srService.approveReady(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_corReady".equals(operationId)) {
			RegMaster result = srService.corReady(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_approve".equals(operationId)) {
			RegMaster result = srService.approve(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_complete".equals(operationId)) {
			//getTx(clientValues)//
			RegMaster result = srService.complete(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_withdraw".equals(operationId) || "RegMasterDS_updateData_reject".equals(operationId)) {
			boolean byApplicant = "RegMasterDS_updateData_withdraw".equals(operationId);
			RegMaster result = srService.withdraw(entity, taskId, byApplicant);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_reset".equals(operationId)) {
			RegMaster result = srService.reset(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_accept_reregdereg".equals(operationId)) { // Re reg De Reg actions
			Boolean reReg = (Boolean) clientValues.get("reReg");
			deRegService.accept(entity, taskId, reReg);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_approve_reregdereg".equals(operationId)) {
			Boolean reReg = (Boolean) clientValues.get("reReg");
			deRegService.approve(taskId, reReg);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_crossCheckReady_reregdereg".equals(operationId)) {
			Boolean reReg = (Boolean) clientValues.get("reReg");
			deRegService.ready(entity, taskId, reReg);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_complete_reregdereg".equals(operationId)) {
			Boolean reReg = (Boolean) clientValues.get("reReg");
			entity = deRegService.complete(entity, taskId, reReg, getTx(clientValues), entity.getDeRegTime());
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_withdraw_reregdereg".equals(operationId)) {
			Boolean reReg = (Boolean) clientValues.get("reReg");
			deRegService.withdraw(taskId, reReg);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_complete_reregnewapp".equals(operationId)) {
			ApplDetail details = toApplDetails(clientValues);
			List<Owner> owners = toOwners(clientValues);
			Representative rep = toRep(clientValues);
			List<BuilderMaker> bmList = toBuilderMakers(clientValues);
			deRegService.completeNew(taskId, entity, details, owners, rep, bmList, getTx(clientValues), entity.getDeRegTime());
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("UPDATE_ISSUE_LOG".equals(operationId)) {
			Map suppliedValues = dsRequest.getClientSuppliedValues();
			//RegMaster record = new RegMaster();
			String applNo = "";
			String regStatus = "";
			int issueOfficeId = 0;
			
			Date issueDate = new Date();
			if (suppliedValues.containsKey("regStatus")) {
				regStatus = suppliedValues.get("regStatus").toString();
			}
			if (!RegMaster.REG_STATUS_DEREGISTERED.equals(regStatus)) {	// only CoR should create log record
				if (suppliedValues.containsKey("applNo")) {
					applNo = suppliedValues.get("applNo").toString();
				}
				if (suppliedValues.containsKey("issueOfficeId")) {
					issueOfficeId = Integer.parseInt(suppliedValues.get("issueOfficeId").toString());
				}
				if (suppliedValues.containsKey("issueDate")){
					issueDate = new SimpleDateFormat("dd/MM/yyyy").parse(suppliedValues.get("issueDate").toString());
				}
				createIssueLog(applNo, regStatus, issueOfficeId, issueDate);
				if (suppliedValues.containsKey("taskId")) {
					taskId = Long.parseLong(suppliedValues.get("taskId").toString());
					if (taskId!=0) {
						inbox.updateParam3(taskId, "CoR Granted");
					}
				}
			}
			
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("REVISE_REG_DATE_TIME".equals(operationId)) {
			Map suppliedValues = dsRequest.getClientSuppliedValues();
			String applNo = "";
			String regDate = "";
			String regTime = "";
			if (suppliedValues.containsKey("applNo")) {
				applNo = suppliedValues.get("applNo").toString();
			}
			if (suppliedValues.containsKey("regDateStr")) {
				regDate = suppliedValues.get("regDateStr").toString();
			}
			if (suppliedValues.containsKey("regTimeStr")) {
				regTime = suppliedValues.get("regTimeStr").toString();
			}
			Date registrationDate = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(regDate + " " + regTime);
			//entity.setRegDate(registrationDate);
			entity = srService.reviseRegDateTimeAndProvExpiryDate(applNo, registrationDate);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("UPDATE_MULTI_TRACK_CODE".equals(operationId)) {
			Map suppliedValues = dsRequest.getClientSuppliedValues();		
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		return super.update(entity, dsRequest);
	}

	private EntityCertIssueLog createIssueLog(String applNo, String regStatus, int issueOfficeId, Date issueDate) {
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		//List<EntityCertIssueLog> entities = certIssueLogDao.get(certApplicationId)
		EntityCertIssueLog log = new EntityCertIssueLog();
		//log.setLogId(-1);
		log.setCertApplicationNo(applNo);
		if ("R".equals(regStatus)) {
			log.setCertType(CertificateTypeEnum.REVISED_COR.toString());
		} else {
			log.setCertType(CertificateTypeEnum.NEW_COR.toString());
		}
		log.setIssueBy(currentUser);
		log.setIssueDate(issueDate);
		log.setIssueOfficeID(issueOfficeId);
		try {
			certIssueLogService.save(log);
			return log;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	private ApplDetail toApplDetails(Map<?, ?> values) {
		ApplDetail details = null;
		if (values.containsKey("applNo")) {
			String applNo = values.get("applNo").toString();
			details = applDetailDao.findById(applNo);		
			if (details==null) {
				details = new ApplDetail();
			}
		} else {
			details = new ApplDetail();
		}
		
		Map<?,?> adMap = (Map<?,?>) values.get("applDetails");
		if (adMap != null) {
			setValues(details, adMap);
		}
		return details;
	}

	private Representative toRep(Map<?, ?> values) {
		Map<?,?> repMap = (Map<?,?>) values.get("representative");
		Representative rep = new Representative();
		setValues(rep, repMap);
		return rep;
	}

	private List<BuilderMaker> toBuilderMakers(Map<?,?> clientValues) {
		List<Map<?, ?>> maps = (List<Map<?,?>>) clientValues.get("builderMakers");
		List<BuilderMaker> list = new ArrayList<>();
		if (maps != null) {
			for (Map<?, ?> map:maps) {
				BuilderMaker bm = new BuilderMaker();
				setValues(bm, map);
				list.add(bm);
			}
		}
		return list;
	}

	private List<Owner> toOwners(Map<?,?> request) {
		List<Map<?, ?>> maps = (List<Map<?,?>>) request.get("owners");
		List<Owner> owners = new ArrayList<>();
		int seq = 1;
		for (Map<?,?> values : maps) {
			Owner owner = new Owner();
			setValues(owner, values);
			owner.setApplNo((String) request.get("applNo"));
			owners.add(owner);
		}
		return owners;
	}

	@Override
	public DSResponse add(RegMaster rm, DSRequest dsRequest) throws Exception {
		String operationId = dsRequest.getOperationId();

		switch (operationId) {
		case "emailCollectCoR": // PRG-SUPP-012	Email Owner to collect CoR
			mailService.collectCor(rm);
			break;
		case "emailClassSocCoR": // PRG-SUPP-013	Email Class Society for new CoR, updated CoR
			Map appDetails = (Map) dsRequest.getClientSuppliedValues().get("applDetails");
			if (appDetails != null) {
				String code = (String) appDetails.get("cs1ClassSocCode");
				if (code != null) {
					ClassSociety soc = classSocDao.findById( code);
					mailService.corNotify(soc, rm);
				}
			}
			break;
		case "emailRegMissingDoc":// PRG-SUPP-014	Email Owner to submit ship registration missing document
			mailService.submitMissingDoc(rm);
			break;
		case "emailOwnerAIP": // PRG-SUPP-015	Email Owner AIP
			mailService.sendOwnerAip(rm, dsRequest.getClientSuppliedValues());
			break;
		case "memoOfcaCoR": // PRG-SUPP-019	Memo to OFCA for change of CoR
			mailService.sendOfcaCorChange(rm);
			break;
		case "memoCosdCoR": // PRG-SUPP-020	Memo to CO/SD for change of CoR
			mailService.sendCoSdCorChange(rm);
			break;
		case "memoOfcaAip": // PRG-SUPP-021	Memo to OFCA for AIP, new CoR, updated CoR
			mailService.sendOfcaNewOrUpdateCorAip(rm);
			break;
		case "memoCosdAip": // PRG-SUPP-022	Memo to CO/SD for AIP, new CoR, updated CoR
			mailService.sendCoSdNewOrUpdateCorAip(rm);
			break;
		default:
			return super.add(rm, dsRequest);
		}
		return new DSResponse(DSResponse.STATUS_SUCCESS);
	}


}
