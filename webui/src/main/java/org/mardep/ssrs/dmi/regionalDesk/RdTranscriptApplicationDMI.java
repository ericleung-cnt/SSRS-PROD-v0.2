package org.mardep.ssrs.dmi.regionalDesk;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.mardep.ssrs.dao.dns.IControlDataDao;
import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.Action;
import org.mardep.ssrs.domain.constant.CertificateTypeEnum;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dns.ControlAction;
import org.mardep.ssrs.domain.dns.ControlData;
import org.mardep.ssrs.domain.dns.ControlEntity;
import org.mardep.ssrs.domain.entity.transcript.EntityTranscriptApplication;
import org.mardep.ssrs.domain.model.transcript.ModelTranscriptApplication;
import org.mardep.ssrs.regionalDeskService.IRdTranscriptApplicationService;
//import org.mardep.ssrs.regionalDeskService.IRdTranscriptApplicationService;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.IDnsService;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class RdTranscriptApplicationDMI {

	@Autowired
	IRdTranscriptApplicationService taSvc;
	
	@Autowired
	IDnsService dnsService;

	@Autowired
	IVitalDocClient vitalDocClient;

	@Autowired
	IControlDataDao controlDataDao;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public DSResponse fetch(ModelTranscriptApplication model, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();

		String operationId = dsRequest.getOperationId();
		if ("CREATE_DEMAND_NOTE_ITEM".equals(operationId)){
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			
			return dsResponse;			
		} else {			
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			List<ModelTranscriptApplication> list = getAll();
			dsResponse.setData(list);
		
			return dsResponse;
		}
	}
	
	public List<ModelTranscriptApplication> getAll(){
		return taSvc.getAll();
	}
	
	private ModelTranscriptApplication save(ModelTranscriptApplication model) throws Exception {
			ModelTranscriptApplication savedModel = taSvc.save(model);
			return savedModel;
	}
	
	public DSResponse update(ModelTranscriptApplication model, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		String operationId = dsRequest.getOperationId();

		try {
			if ("CREATE_DEMAND_NOTE_ITEM".equals(operationId)){
				ModelTranscriptApplication savedModel = save(model);
				createCertDemandNoteItems(savedModel);
				dsResponse.setData(savedModel);
			} else if ("TRANSCRIPT_CREATE_DEMAND_NOTE".equals(operationId)) {
				Map suppliedValues = dsRequest.getClientSuppliedValues();
				Integer applicationId = Integer.parseInt(suppliedValues.get("id").toString());
				Integer officeId = 0;
				String certified = "";
				
				//Map<Object, Object> dnUiMap = (Map<Object, Object>)suppliedValues.get("dn"); 
				DemandNoteHeader dnUiForm = new DemandNoteHeader();
				if (suppliedValues.containsKey("address1")) 
					dnUiForm.setAddress1(suppliedValues.get("address1") == null ? "" : suppliedValues.get("address1").toString());
				if (suppliedValues.containsKey("address2")) 
					dnUiForm.setAddress2(suppliedValues.get("address2")==null ? "" : suppliedValues.get("address2").toString());
				if (suppliedValues.containsKey("address3")) 
					dnUiForm.setAddress3(suppliedValues.get("address3")==null ? "" : suppliedValues.get("address3").toString());
				if (suppliedValues.containsKey("applNo")) 
					dnUiForm.setApplNo(suppliedValues.get("applNo") == null ? "" : suppliedValues.get("applNo").toString());
				if (suppliedValues.containsKey("billName")) 
					dnUiForm.setBillName(suppliedValues.get("billName") == null ? "" : suppliedValues.get("billName").toString());
				if (suppliedValues.containsKey("coName")) {
					if (suppliedValues.get("coName")!=null)	dnUiForm.setCoName(suppliedValues.get("coName").toString());
				}
				if (suppliedValues.containsKey("officeId")) {
					if (suppliedValues.get("officeId")!=null) officeId = Integer.parseInt(suppliedValues.get("officeId").toString()); 
				}
				if (suppliedValues.containsKey("dueDate")) {
					//ZonedDateTime zdt = ZonedDateTime.parse(dnUiMap.get("dueDate").toString(), DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss 'CST' yyyy"));
					//dnUiForm.setDueDate(new SimpleDateFormat("dd/MM/yyyy").parse(dnUiMap.get("dueDate").toString()));
					//dnUiForm.setDueDate(Date.from(zdt.toInstant()));
					dnUiForm.setDueDate(new SimpleDateFormat("dd/MM/yyyy").parse(suppliedValues.get("dueDate").toString()));
				}
				if (suppliedValues.containsKey("certified")) {
					certified = "TRUE".equals(suppliedValues.get("certified").toString()) ? CertificateTypeEnum.CERTIFIED_TRANSCRIPT.toString() : CertificateTypeEnum.UNCERTIFIED_TRANSCRIPT.toString();
				}
				DemandNoteHeader dnHeader = createDemandNote(model.getId(), certified, dnUiForm, officeId);// model.getApplNo());
				if (dnHeader!=null) {
					//svc.removeCertDemandNoteItems(model.getId(), dnHeader.getDemandNoteNo());
					dsResponse.setData(dnHeader);					
				}
			} else if ("UPDATE_ISSUE_LOG".equals(operationId)) {
				Map suppliedValues = dsRequest.getClientSuppliedValues();
				Integer applicationId = Integer.parseInt(suppliedValues.get("id").toString());
				String applNo = suppliedValues.get("applNo").toString();
				Integer officeId = Integer.parseInt(suppliedValues.get("issueOffice").toString());
				Date issueDate = new Date();
				String issueBy = suppliedValues.get("userId").toString();
				String certified = "TRUE".equals(suppliedValues.get("certified").toString()) ? CertificateTypeEnum.CERTIFIED_TRANSCRIPT.toString() : CertificateTypeEnum.UNCERTIFIED_TRANSCRIPT.toString();
				taSvc.createCertIssueLogInfo(applicationId, certified, applNo, officeId, issueDate, issueBy);
			} else if ("UPDATE_TRANSCRIPT".equals(operationId)){			
				ModelTranscriptApplication savedModel = save(model);
				dsResponse.setData(savedModel);
			}
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;			//EEE MMM d HH:mm:ss CST yyyy
		} catch (Exception ex) {
			logger.error("Fail to update-{}, Exception-{}", new Object[]{model, ex}, ex);			
			dsResponse.setStatus(DSResponse.STATUS_FAILURE);
			return dsResponse;				
		}	
	}
	
	public DSResponse add(ModelTranscriptApplication model, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		ModelTranscriptApplication savedModel;
		try {
			savedModel = save(model);
			dsResponse.setData(savedModel);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;			//EEE MMM d HH:mm:ss CST yyyy
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dsResponse.setStatus(DSResponse.STATUS_FAILURE);
			return dsResponse;	
		}
	}
//	public DSResponse add(ModelTranscriptApplication model, DSRequest dsRequest) {
//		DSResponse dsResponse = new DSResponse();
//		String operationId = dsRequest.getOperationId();
//
//		try {
//			ModelTranscriptApplication savedModel = save(model);
//			if ("CREATE_DEMAND_NOTE_ITEM".equals(operationId)){
//				createCertDemandNoteItems(savedModel);
//			}
//			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
//			return dsResponse;
//		} catch (Exception ex) {
//			logger.error("Fail to add-{}, Exception-{}", new Object[]{model, ex}, ex);
//			dsResponse.setStatus(DSResponse.STATUS_FAILURE);
//			return dsResponse;
//		}
//	}
//	
	public void createCertDemandNoteItems(ModelTranscriptApplication model) {
		try {
			taSvc.createCertDemandNoteItems(model);
		} catch (Exception ex) {
			logger.error("Fail to add-{}, Exception-{}", new Object[]{model, ex}, ex);
		}
	}
	
	public DemandNoteHeader createDemandNote(Integer applicationId, String certType, DemandNoteHeader dnUiForm, Integer officeId) {// String applNo) {
//		DSResponse dsResponse = new DSResponse();
//		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
//			ModelTranscriptApplication savedModel;
//			if (model.getId()==null) {
//				savedModel = svc.save(model);
//			} else {
//				savedModel = model;
//			}
			DemandNoteHeader dnHeader = taSvc.createDemandNote(applicationId, certType, dnUiForm, officeId); //applNo);
			sendToDNS_DMS(dnHeader.getDemandNoteNo());
			return dnHeader;
		} catch (Exception ex) {
			logger.error("Fail to add-{}, Exception-{}", new Object[]{applicationId, ex}, ex);
			//dsResponse.setStatus(DSResponse.STATUS_FAILURE);
			return null;
		}		
	}

	private void sendToDNS_DMS(String demandNoteNo) {
		dnsService.createDemandNote(demandNoteNo, Action.U, false);
		ControlData controlData = controlDataDao.find(ControlEntity.DN, ControlAction.CREATE, demandNoteNo);

		byte[] osContent = controlData.getFile();
		if(osContent!=null){
			try {
				vitalDocClient.uploadDemandNote(demandNoteNo, osContent);
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				logger.error("Fail to add-{}, Exception-{}", new Object[]{demandNoteNo, ex}, ex);
				ex.printStackTrace();
			}
		}
	}
	}
