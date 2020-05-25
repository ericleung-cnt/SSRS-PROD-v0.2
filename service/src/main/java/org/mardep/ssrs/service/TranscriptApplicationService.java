package org.mardep.ssrs.service;

import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrTranscriptDao;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TranscriptApplicationService implements ITranscriptApplicationService {

	@Autowired
	IOcrTranscriptDao transcriptDao;
	
	@Override
	public List<OcrEntityTranscript> getAll() {
		// TODO Auto-generated method stub
		return transcriptDao.getAll();
	}

	@Override
	public void remove(int id) {
		// TODO Auto-generated method stub
		transcriptDao.remove(id);
	}

	@Override
	public OcrEntityTranscript save(OcrEntityTranscript entity) {
		// TODO Auto-generated method stub
		//return null;
		return transcriptDao.save(entity);
	}

	@Override
	public OcrEntityTranscript getById(int id) {
		// TODO Auto-generated method stub
		return transcriptDao.findById(id);
	}

}
