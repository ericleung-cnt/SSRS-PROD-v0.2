package org.mardep.ssrs.dmi.ocr;

import java.util.List;

import org.mardep.ssrs.dao.ocr.IOcrTranscriptDao;
import org.mardep.ssrs.dmi.codetable.AbstractCodeTableDMI;
import org.mardep.ssrs.domain.codetable.FinanceCompany;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;
import org.mardep.ssrs.service.ITranscriptApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class transcriptApplicationDMI {

	//@Autowired
	//IOcrTranscriptDao transcriptDao;
	
	@Autowired
	ITranscriptApplicationService svc;
	
	public DSResponse fetch(OcrEntityTranscript entity, DSRequest dsRequest) {
		//return super.fetch(entity, dsRequest);
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		List<OcrEntityTranscript> list = getAll();
		dsResponse.setData(list);
		return dsResponse;
	}
	
	public List<OcrEntityTranscript> getAll() {
		//return transcriptDao.getAll();
		return svc.getAll();
	}
		
	public DSResponse remove(OcrEntityTranscript entity) {
		//transcriptDao.remove(entity.getOcrTranscriptId());
		// 2019.08.09 svc.remove(entity.getOcrTranscriptId());
		DSResponse dsResponse = new DSResponse();
		OcrEntityTranscript transcript = svc.getById(entity.getId());
		if (transcript!=null) {
			transcript.setProcessed("Y");
			svc.save(transcript);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		} else {
			dsResponse.setStatus(DSResponse.STATUS_FAILURE);			
		}
		
		return dsResponse; 
	}
}
