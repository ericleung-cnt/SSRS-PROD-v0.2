package org.mardep.ssrs.dmi.ocr;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscriptApplicant;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.ITranscriptApplicantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class TranscriptApplicantDMI extends AbstractDMI<OcrEntityTranscriptApplicant>{

	@Autowired
	ITranscriptApplicantService transcriptApplicantService;
	
	@Override
	protected IBaseService getBaseService() {
		// TODO Auto-generated method stub
		return transcriptApplicantService;
	}

	public DSResponse fetch(OcrEntityTranscriptApplicant entity, DSRequest dsRequest) {
		return super.fetch(entity, dsRequest);
	}
	
	public DSResponse update(OcrEntityTranscriptApplicant entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			return super.update(entity, dsRequest);
		} catch (Exception ex) {
			logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
		
	}
	
	public DSResponse add(OcrEntityTranscriptApplicant entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			return super.add(entity, dsRequest);
		} catch (Exception ex) {
			logger.error("Fail to add-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
		
	}
	
}
