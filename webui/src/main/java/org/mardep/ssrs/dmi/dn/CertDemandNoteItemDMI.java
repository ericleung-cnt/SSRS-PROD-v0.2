package org.mardep.ssrs.dmi.dn;

import java.util.ArrayList;
import java.util.List;

import org.mardep.ssrs.certService.ICertDemandNoteItemService;
import org.mardep.ssrs.domain.constant.CertificateTypeEnum;
import org.mardep.ssrs.domain.entity.cert.EntityCertDemandNoteItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CertDemandNoteItemDMI {
	
	@Autowired
	ICertDemandNoteItemService svc;
	
	public DSResponse fetch(EntityCertDemandNoteItem entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		List<EntityCertDemandNoteItem> entityList = new ArrayList<EntityCertDemandNoteItem>();
		if ("Transcript".equals(entity.getCertType())){
			List<EntityCertDemandNoteItem> certifiedTranscriptList = svc.getByApplicationIdCertType(CertificateTypeEnum.CERTIFIED_TRANSCRIPT.toString(),  entity.getCertApplicationId());
			List<EntityCertDemandNoteItem> uncertifiedTranscriptList = svc.getByApplicationIdCertType(CertificateTypeEnum.UNCERTIFIED_TRANSCRIPT.toString(),  entity.getCertApplicationId());
			if (uncertifiedTranscriptList.size()>0) {
				entityList.addAll(uncertifiedTranscriptList);
			}
			if (certifiedTranscriptList.size()>0) {
				entityList.addAll(certifiedTranscriptList);
			}
		}
		//List<EntityCertDemandNoteItem> entityList = svc.getByApplicationIdCertType(entity.getCertType(),  entity.getCertApplicationId());
		dsResponse.setData(entityList);
		
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		return dsResponse;
	}
	
	
}
