package org.mardep.ssrs.dms.ocr.dbService;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrTranscriptApplicantDao;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscriptApplicant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OcrDbServiceTranscriptApplicant implements IOcrDbServiceTranscriptApplicant {

	@Autowired
	IOcrTranscriptApplicantDao dao;
	
	@Override
	public void save(OcrEntityTranscriptApplicant entity) {
		// TODO Auto-generated method stub
		dao.save(entity);
	}

	@Override
	public void saveIfNotExist(OcrEntityTranscriptApplicant entity) {
		// TODO Auto-generated method stub
		dao.saveIfNotExist(entity);
	}


}
