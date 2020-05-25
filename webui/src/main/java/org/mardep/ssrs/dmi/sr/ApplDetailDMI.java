package org.mardep.ssrs.dmi.sr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FilenameUtils;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ApplDetailDMI extends AbstractSrDMI<ApplDetail> {

	private final String KEY_BUILDER_CERT_DOC = "Builder's Certificate";
	private final String KEY_BILL_OF_SALE_DOC = "Bill of Sale";
	private final String KEY_OWNER_CO_REG_DOC = "Owner company registration document (copy)";
	private final String KEY_FORM_AUTH_DOC = "Form of authority of POA (Owner)";
	private final String KEY_ENTITLE_DOC = "Declaration of Entitlement";
	private final String KEY_CERT_OF_DELETION_DOC = "Certificate of Deletion";
	private final String KEY_CERT_OF_SURVEY_DOC = "Certificate of Survey";
	private final String KEY_PRQC_CONFIRMATION_DOC = "PRQC confirmation";
	private final String KEY_COURT_ORDER_DOC = "Court order of acquisition agreement";
	private final String KEY_DECLARATION_MARKING_DOC = "Declaration/Certification of marking";
	private final String KEY_PDA_DOC = "Delivery confirmation (i.e. Protocol of PA)";
	private final String KEY_DC_DECLARATION_DOC = "DC Declaration of No-Seal";
	private final String KEY_APPLICATION_FORM_DOC = "Application for Registration";
	private final String KEY_DC_BUSINESS_REG_DOC = "DC business registration (copy)";
	private final String KEY_DC_COMPANY_REG_DOC = "DC company registration document(copy)";
	private final String KEY_DC_FORM_AUTH_DOC = "Form of Authority of POA (Demise charterer)";
	private final String KEY_RP_COMPANY_REG_DOC = "RP company registration document (copy)";
	private final String KEY_RP_BUSINESS_REG_DOC = "RP business registration (copy)";
	private final String KEY_EVIDENCE_DELETION_DOC = "Evidence of Deletion";
	private final String KEY_ITC_FOR_PROREG_DOC = "ITC for Pro-Reg only";
	private final String KEY_CERT_OF_OWNERSHIP_DOC = "Certificate of Ownership";
	private final String KEY_OWNER_BUSINESS_REG_DOC = "Owner business registration (copy)";
	private final String KEY_BILL_OF_SALE_ATTORNEY_DOC = "Bill of Sale w/ Power of attorney";
	private final String KEY_BUILDER_CERT_ATTORNEY_DOC = "Builder's Certificate w/ Power of attorney";
	private final String KEY_BUILDER_DECLARATION_NOSEAL_DOC = "Builder Declaration of No-Seal";
	private final String KEY_CERT_DELETION_2REG_DOC = "Certificate of Deletion (Second registry)";
	private final String KEY_CERT_OWNERSHIP_2REG_DOC = "Certificate of Ownership (Second registry)";
	private final String KEY_EVIDENCE_DELETION_2REG_DOC = "Evidence of Deletion (Second registry)";
	private final String KEY_SELLER_DECLARATION_NOSEAL_DOC = "Seller Declaration of No-Seal";
	private final String KEY_OWNER_DECLARATION_NOSEAL_DOC = "Owner Declaration of No-Seal";
	private final String KEY_OWNER_ENCUM_DOC = "Certification of Ownership proving encumbrance status";
	private final String KEY_MORTGAGEE_CONSENT_DOC = "Mortgagee consent for transferring to HK";
			
	@Autowired
	IVitalDocClient vd;
	@Autowired
	IRegMasterDao rmDao;

	@Override
	public DSResponse fetch(ApplDetail entity, DSRequest dsRequest){
		DSResponse dsResponse = super.fetch(entity, dsRequest);
		return dsResponse;
	}

	@Override
	public DSResponse update(ApplDetail entity, DSRequest dsRequest) throws Exception {
		String operationId = dsRequest.getOperationId();
		Map clientSuppliedValues = dsRequest.getClientSuppliedValues();
		if ("updateDocChecklist".equals(operationId)) {
			DSResponse dsResponse = new DSResponse();
			for (Object keyObj : clientSuppliedValues.keySet()) {
				String key = (String) keyObj;
				String prefix = "content_upload_";
				if (key.startsWith(prefix)) {
					String str = (String) clientSuppliedValues.get(key);
					// data:application/vnd.ms-excel;base64,...
					byte[] decoded = new byte[0];
					if (str.startsWith("data:")) {
						int base64 = str.indexOf(";base64,");
						int plain = str.indexOf(",");
						if (base64 > -1) {
							decoded = Base64.getDecoder().decode(str.substring(base64 + 8).getBytes());
						} else if (plain > -1){
							decoded = str.substring(plain + 1).getBytes();
						}
					}
					// send decoded to dms
					RegMaster reg = rmDao.findById(entity.getApplNo());
					try {
						vd.uploadSrSupporting(reg.getImoNo(), reg.getOffNo(), key.substring(prefix.length()), decoded);
						fillDocName(entity, clientSuppliedValues);
					} catch (Exception ex) {
						logger.error(ex.getMessage());
						dsResponse.setStatus(DSResponse.STATUS_FAILURE);
						dsResponse.setFailure("File upload unsucceed, pls try again later");
						return dsResponse;
					}
				}
			}
			//return super.update(entity, dsRequest);
			dsResponse =  super.update(entity, dsRequest);
			return dsResponse;
		}
		return super.update(entity, dsRequest);
	}

	private void fillDocName(ApplDetail entity, Map clientSuppliedValues) {
		for (Object keyObj : clientSuppliedValues.keySet()) {
			String key = (String) keyObj;
			String prefix = "filename_upload_";
			if (key.startsWith(prefix)) {
				String str = (String) clientSuppliedValues.get(key);
				String subkey = key.substring(16, key.length());
				//File f = new File(str);
				//String filename = f.getName();
				String filename = FilenameUtils.getName(str);
				if (KEY_BUILDER_CERT_DOC.equals(subkey)){
					entity.setBuilderCertDoc(filename);
				} else if (KEY_BILL_OF_SALE_DOC.equals(subkey)){
					entity.setBillOfSaleDoc(filename);
				} else if (KEY_OWNER_CO_REG_DOC.equals(subkey)){
					entity.setOwnerCoRegDoc(filename);
				} else if (KEY_FORM_AUTH_DOC.equals(subkey)){
					entity.setFormAuthDoc(filename);
				} else if (KEY_ENTITLE_DOC.equals(subkey)){
					entity.setEntitleDoc(filename);
				} else if (KEY_CERT_OF_DELETION_DOC.equals(subkey)){
					entity.setCertOfDeletionDoc(filename);
				} else if (KEY_CERT_OF_SURVEY_DOC.equals(subkey)){
					entity.setCertOfSurveyDoc(filename);					
				} else if (KEY_PRQC_CONFIRMATION_DOC.equals(subkey)){
					entity.setPrqcConfirmationDoc(filename);
				} else if (KEY_COURT_ORDER_DOC.equals(subkey)){
					entity.setCourtOrderDoc(filename);
				} else if (KEY_DECLARATION_MARKING_DOC.equals(subkey)){
					entity.setDeclarationMarkingDoc(filename);
				} else if (KEY_PDA_DOC.equals(subkey)){
					entity.setPdaDoc(filename);
				} else if (KEY_DC_DECLARATION_DOC.equals(subkey)){
					entity.setDcDeclaratioDoc(filename);
				} else if (KEY_APPLICATION_FORM_DOC.equals(subkey)){
					entity.setApplicationFormDoc(filename);
				} else if (KEY_DC_BUSINESS_REG_DOC.equals(subkey)){
					entity.setDcBusinessRegDoc(filename);
				} else if (KEY_DC_COMPANY_REG_DOC.equals(subkey)){
					entity.setDcCompanyRegDoc(filename);
				} else if (KEY_DC_FORM_AUTH_DOC.equals(subkey)){
					entity.setDcFormAuthDoc(filename);
				} else if (KEY_RP_COMPANY_REG_DOC.equals(subkey)){
					entity.setRpBusinessRegDoc(filename);
				} else if (KEY_RP_BUSINESS_REG_DOC.equals(subkey)){
					entity.setRpBusinessRegDoc(filename);
				} else if (KEY_EVIDENCE_DELETION_DOC.equals(subkey)){
					entity.setEvidenceDeletionDoc(filename);
				} else if (KEY_ITC_FOR_PROREG_DOC.equals(subkey)){
					entity.setItcForProRegDoc(filename);
				} else if (KEY_CERT_OF_OWNERSHIP_DOC.equals(subkey)){
					entity.setCertOfOwnershipDoc(filename);
				} else if (KEY_OWNER_BUSINESS_REG_DOC.equals(subkey)){
					entity.setOwnerBusinessRegDoc(filename);
				} else if (KEY_BILL_OF_SALE_ATTORNEY_DOC.equals(subkey)){
					entity.setBillOfSaleAttorneyDoc(filename);
				} else if (KEY_BUILDER_CERT_ATTORNEY_DOC.equals(subkey)){
					entity.setBuilderCertAttorneyDoc(filename);					
				} else if (KEY_BUILDER_DECLARATION_NOSEAL_DOC.equals(subkey)){
					entity.setBuilderDeclarationNosealDoc(filename);
				} else if (KEY_CERT_DELETION_2REG_DOC.equals(subkey)){
					entity.setCertDeletion2RegDoc(filename);
				} else if (KEY_CERT_OWNERSHIP_2REG_DOC.equals(subkey)){
					entity.setCertOwnership2RegDoc(filename);
				} else if (KEY_EVIDENCE_DELETION_2REG_DOC.equals(subkey)){
					entity.setEvidenceDeletion2RegDoc(filename);
				} else if (KEY_SELLER_DECLARATION_NOSEAL_DOC.equals(subkey)){
					entity.setSellerDeclarationNosealDoc(filename);
				} else if (KEY_OWNER_DECLARATION_NOSEAL_DOC.equals(subkey)){
					entity.setOwnerDeclarationNosealDoc(filename);
				} else if (KEY_OWNER_ENCUM_DOC.equals(subkey)){
					entity.setOwnerEncumDoc(filename);
				} else if (KEY_MORTGAGEE_CONSENT_DOC.equals(subkey)) {
					entity.setMortgageeConsentDoc(filename);
				}
			}
		}
	}
	
	@Override
	public DSResponse add(ApplDetail entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}


}
