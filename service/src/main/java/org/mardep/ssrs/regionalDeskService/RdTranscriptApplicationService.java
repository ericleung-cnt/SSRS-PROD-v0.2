package org.mardep.ssrs.regionalDeskService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.mardep.ssrs.certService.ICertDemandNoteItemService;
import org.mardep.ssrs.dao.cert.ICertDemandNoteItemDao;
import org.mardep.ssrs.dao.cert.ICertDemandNoteLogDao;
import org.mardep.ssrs.dao.cert.ICertIssueLogDao;
import org.mardep.ssrs.dao.codetable.IFeeCodeDao;
import org.mardep.ssrs.dao.codetable.IOfficeDao;
import org.mardep.ssrs.dao.transcript.ITranscriptApplicationDao;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.codetable.Office;
import org.mardep.ssrs.domain.constant.CertificateTypeEnum;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.entity.cert.EntityCertDemandNoteItem;
import org.mardep.ssrs.domain.entity.cert.EntityCertDemandNoteLog;
import org.mardep.ssrs.domain.entity.cert.EntityCertIssueLog;
import org.mardep.ssrs.domain.entity.transcript.EntityTranscriptApplication;
import org.mardep.ssrs.domain.model.transcript.ModelTranscriptApplication;
import org.mardep.ssrs.service.AbstractService;
import org.mardep.ssrs.service.IDemandNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RdTranscriptApplicationService extends AbstractService implements IRdTranscriptApplicationService {

	@Autowired
	ITranscriptApplicationDao transcriptApplicationDao;
	
//	@Autowired
//	ICertDemandNoteItemDao certDemandNoteItemDao;

	@Autowired
	ICertDemandNoteItemService certDemandNoteItemSvc;
	
	@Autowired
	ICertDemandNoteLogDao certDemandNoteLogDao;
	
	@Autowired
	ICertIssueLogDao certIssueLogDao;
	
	@Autowired
	IOfficeDao officeDao;
	
	@Autowired
	@Qualifier("demandNoteService")
	IDemandNoteService dnSvc;
	
	@Autowired
	IFeeCodeDao feeCodeDao;
	
	@Override
	public List<ModelTranscriptApplication> getAll() {
		// TODO Auto-generated method stub
		List<ModelTranscriptApplication> modelList = new ArrayList<ModelTranscriptApplication>();
		List<EntityTranscriptApplication> transcriptApplicationList = transcriptApplicationDao.getAll();
		if (transcriptApplicationList.size()>0) {
			for (EntityTranscriptApplication t:transcriptApplicationList) {
				ModelTranscriptApplication model = new ModelTranscriptApplication();
				
				model.setApplNo(t.getApplNo());
				model.setCertified(t.getCertified());
				model.setDelayPaymentRequired(t.getDelayPaymentRequired());
				model.setDelayPaymentReason(t.getDelayPaymentReason());
				model.setId(t.getId());
				model.setNoPaymentReason(t.getNoPaymentReason());
				model.setPaymentRequired(t.getPaymentRequired());
				model.setOfficeCode(t.getOfficeCode());
				model.setOfficeId(t.getOfficeId());
				model.setOfficialNo(t.getOfficialNo());
				model.setImoNo(t.getImoNO());
				model.setShipNameEng(t.getShipNameEng());
				model.setShipNameChi(t.getShipNameChi());
				model.setRegistrar(t.getRegistrar());
				model.setReportDate(t.getReportDate());
				model.setReportGenBy(t.getReportGenBy());
				
				insertCertDemandNoteLogInfo(model);
				insertCertIssueLogInfo(model);
				
				modelList.add(model);
			}
		}
		return modelList;
	}

	public void insertCertDemandNoteLogInfo(ModelTranscriptApplication model) {
		List<EntityCertDemandNoteLog> logList = certDemandNoteLogDao.get(model.getId());
		if (logList.size()>0) {
			EntityCertDemandNoteLog log = logList.get(0);
			if (log.getDemandNoteNo()!=null) {
				model.setDemandNoteNo(log.getDemandNoteNo());
				DemandNoteHeader header = dnSvc.getDemandNoteHeader(log.getDemandNoteNo());
				if (header!=null) {
					model.setDemandNotePaymentStatus(header.getPaymentStatus());
				}
			}
		}
	}
	
	public void createCertDemandNoteLogInfo(Integer applicationId, String certType, String applNo, String dnNo) {
		EntityCertDemandNoteLog entity = new EntityCertDemandNoteLog();
		entity.setCertApplicationId(applicationId);
		//entity.setCertType(CertificateTypeEnum.TRANSCRIPT.toString());
		entity.setCertType(certType);
		entity.setDemandNoteNo(dnNo);
		certDemandNoteLogDao.save(entity);
	}
	
	public void insertCertIssueLogInfo(ModelTranscriptApplication model) {
//		List<EntityCertIssueLog> logList = certIssueLogDao.get(CertificateTypeEnum.TRANSCRIPT.toString(), model.getId());
		List<EntityCertIssueLog> logList = certIssueLogDao.get(model.getId());
		if (logList.size()>0) {
			EntityCertIssueLog log = logList.get(0);
			model.setIssueDate(log.getIssueDate());
			model.setIssueOffice(log.getIssueOffice());
			model.setIssueOfficeId(log.getIssueOfficeID());
			model.setIssueBy(log.getIssueBy());
		}
	}
	
	@Override
	public EntityCertIssueLog createCertIssueLogInfo(Integer applicationId, String certType, String applNo, int officeId, Date issueDate, String issueBy) {
		//EntityCertIssueLog entity = new EntityCertIssueLog();
		//entity.setCertApplicationId(applicationId);
		List<EntityCertIssueLog> entities = certIssueLogDao.get(applicationId);
		EntityCertIssueLog entity;
		if (entities.size()>0) {
			entity = entities.get(0);
		} else {
		//if (entity==null) {
			entity = new EntityCertIssueLog();
			entity.setCertApplicationId(applicationId);
		//}
		}
		Office issueOffice = officeDao.findById(officeId);
		entity.setCertApplicationNo(applNo);
		//entity.setCertType(CertificateTypeEnum.TRANSCRIPT.toString());
		entity.setCertType(certType);
		entity.setIssueBy(issueBy);
		entity.setIssueDate(issueDate);
		entity.setIssueOffice(issueOffice.getCode());
		entity.setIssueOfficeID(issueOffice.getId());
		certIssueLogDao.save(entity);
		
		return entity;
	}
	
	@Override
	public ModelTranscriptApplication save(ModelTranscriptApplication model) {
		// TODO Auto-generated method stub
		EntityTranscriptApplication entity;
		if (model.getId()!=null && model.getId()>0) {
			entity = transcriptApplicationDao.get(model.getId());
		} else {
			entity = new EntityTranscriptApplication();
		}
		entity.setApplNo(model.getApplNo());
		entity.setCertified(model.getCertified());
		entity.setDelayPaymentRequired(model.getDelayPaymentRequired());
		entity.setDelayPaymentReason(model.getDelayPaymentReason());
		//entity.setId(model.getId());
		entity.setNoPaymentReason(model.getNoPaymentReason());
		entity.setPaymentRequired(model.getPaymentRequired());
		entity.setOfficeCode(model.getOfficeCode());
		entity.setOfficeId(model.getOfficeId());
		entity.setOfficialNo(model.getOfficialNo());
		entity.setImoNO(model.getImoNo());
		entity.setShipNameEng(model.getShipNameEng());
		entity.setShipNameChi(model.getShipNameChi());
		entity.setRegistrar(model.getRegistrar());
		entity.setReportDate(model.getReportDate());
		entity.setReportGenBy(model.getReportGenBy());
		
		EntityTranscriptApplication savedEntity = transcriptApplicationDao.save(entity);
		model.setId(savedEntity.getId());
		
		insertCertDemandNoteLogInfo(model);
		insertCertIssueLogInfo(model);

		return model;
	}

	@Override
	public List<EntityCertDemandNoteItem> createCertDemandNoteItems(ModelTranscriptApplication model) {
		// TODO Auto-generated method stub
		List<EntityCertDemandNoteItem> itemList; 
		itemList = certDemandNoteItemSvc.get(model.getId());
		if (itemList.size()>0) {
			EntityCertDemandNoteItem itemTranscriptCertification = itemList
					.stream()
					.filter(d->d.getFcFeeCode().equals(FeeCode.TRANSCRIPT_CERTIFICATION))
					.findAny()
					.orElse(null);
					
			if (model.getCertified()) {
				// if user changed from non-certified to certified, add fee item transcript certification to item list
				if (itemTranscriptCertification == null) {		
					itemList.add(createCertDemandNoteItemForCertifiedTranscript(model.getId()));
				}
			} else {
				// if user changed from certified to non certified, remove transcript certification from item list and db
				itemList.removeIf(d->d.getFcFeeCode().equals(FeeCode.TRANSCRIPT_CERTIFICATION));
				if (itemTranscriptCertification!=null) {
					certDemandNoteItemSvc.delete(itemTranscriptCertification);
				}
			}
		} else {
			itemList = new ArrayList<EntityCertDemandNoteItem>();
		
			itemList.add(createCertDemandNoteItemForUncertitiedTranscript(model.getId()));
			if (model.getCertified()) {
				itemList.add(createCertDemandNoteItemForCertifiedTranscript(model.getId()));
			}
		}
		return itemList;
	}
	
	//@Override
	public void removeCertDemandNoteItems(Integer applicationId,String certType, String demandNoteNo) {
		//List<EntityCertDemandNoteItem> itemList = certDemandNoteItemDao.get(CertificateTypeEnum.TRANSCRIPT.toString(), applicationId);
		//List<EntityCertDemandNoteItem> itemList = certDemandNoteItemDao.get(certType, applicationId);
		List<EntityCertDemandNoteItem> itemList = certDemandNoteItemSvc.getByApplicationIdCertType(certType, applicationId);
		for (EntityCertDemandNoteItem item : itemList) {
			item.setDemandNoteNo(demandNoteNo);
			certDemandNoteItemSvc.save(item);
		}
	}
	
	// fee code = 11
	public EntityCertDemandNoteItem createCertDemandNoteItemForUncertitiedTranscript(Integer certApplicationId) {
		EntityCertDemandNoteItem dni = new EntityCertDemandNoteItem();
		
		FeeCode fee = feeCodeDao.findById(FeeCode.TRANSCRIPT);
		dni.setAmount(fee.getFeePrice());
		dni.setCertApplicationId(certApplicationId);
		dni.setCertType(CertificateTypeEnum.UNCERTIFIED_TRANSCRIPT.toString());
		dni.setFcFeeCode(FeeCode.TRANSCRIPT);
		
		EntityCertDemandNoteItem savedDni = certDemandNoteItemSvc.save(dni);
		
		return savedDni;
	}
	
	// fee code = 12
	public EntityCertDemandNoteItem createCertDemandNoteItemForCertifiedTranscript(Integer certApplicationId) {	
		EntityCertDemandNoteItem dni = new EntityCertDemandNoteItem();
		
		FeeCode fee = feeCodeDao.findById(FeeCode.TRANSCRIPT_CERTIFICATION);
		dni.setAmount(fee.getFeePrice());
		dni.setCertApplicationId(certApplicationId);
		dni.setCertType(CertificateTypeEnum.CERTIFIED_TRANSCRIPT.toString());
		dni.setFcFeeCode(FeeCode.TRANSCRIPT_CERTIFICATION);

		EntityCertDemandNoteItem savedDni = certDemandNoteItemSvc.save(dni);
		
		return savedDni;
	}
	
	@Override
	public DemandNoteHeader createDemandNote(Integer applicationId, String certType, DemandNoteHeader dnUi, Integer officeId) { // String applNo) {
		try {
			Date generationTime = new Date();
			BigDecimal totalAmt = new BigDecimal(0);
			String officeDnCode = officeDao.findById(officeId).getDnCode();
			List<EntityCertDemandNoteItem> certDemandNoteItemList = new ArrayList<EntityCertDemandNoteItem>(); 
					
			if (CertificateTypeEnum.UNCERTIFIED_TRANSCRIPT.toString().equals(certType)
					|| CertificateTypeEnum.CERTIFIED_TRANSCRIPT.toString().equals(certType)) {
				List<EntityCertDemandNoteItem> uncertifiedTranscriptList = certDemandNoteItemSvc.getByApplicationIdCertType(CertificateTypeEnum.UNCERTIFIED_TRANSCRIPT.toString(),  applicationId);				
				List<EntityCertDemandNoteItem> certifiedTranscriptList = certDemandNoteItemSvc.getByApplicationIdCertType(CertificateTypeEnum.CERTIFIED_TRANSCRIPT.toString(),  applicationId);
				if (uncertifiedTranscriptList.size()>0) {
					certDemandNoteItemList.addAll(uncertifiedTranscriptList);
				}
				if (certifiedTranscriptList.size()>0) {
					certDemandNoteItemList.addAll(certifiedTranscriptList);
				}
			} else {				
				//certDemandNoteItemList = certDemandNoteItemDao.get(certType, applicationId);
				certDemandNoteItemList = certDemandNoteItemSvc.getByApplicationIdCertType(certType, applicationId);
			}
			if (certDemandNoteItemList.size()>0) {
				DemandNoteHeader dnHeader = new DemandNoteHeader();
				dnHeader.setDemandNoteItems(new ArrayList<DemandNoteItem>());
				dnHeader.setApplNo(dnUi.getApplNo());
				dnHeader.setBillName(dnUi.getBillName());
				dnHeader.setCoName(dnUi.getCoName());
				dnHeader.setAddress1(dnUi.getAddress1());
				dnHeader.setAddress2(dnUi.getAddress2());
				dnHeader.setAddress3(dnUi.getAddress3());
				dnHeader.setGenerationTime(generationTime);
				dnHeader.setDueDate(dnUi.getDueDate());
				dnHeader.setStatus(DemandNoteHeader.STATUS_ISSUED);
				dnHeader.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_OUTSTANDING);
				
				for (EntityCertDemandNoteItem item:certDemandNoteItemList) {
					DemandNoteItem dnItem = new DemandNoteItem();
					dnItem.setApplNo(dnUi.getApplNo());	// get application number from model
					dnItem.setActive(new Boolean(true));
					dnItem.setAmount(item.getAmount());
					dnItem.setFcFeeCode(item.getFcFeeCode());
					dnItem.setGenerationTime(generationTime);
					dnItem.setChgIndicator("Y");
					dnItem.setChargedUnits(1);
					
					totalAmt.add(item.getAmount());
					
					dnHeader.getDemandNoteItems().add(dnItem);
				}
				dnHeader.setAmount(totalAmt);
				DemandNoteHeader savedDemandNote = dnSvc.create(dnHeader, officeDnCode, false);
				removeCertDemandNoteItems(applicationId, certType, savedDemandNote.getDemandNoteNo());
				createCertDemandNoteLogInfo(applicationId, certType, savedDemandNote.getApplNo(), savedDemandNote.getDemandNoteNo());
				return savedDemandNote;
			}
			return null;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

}
