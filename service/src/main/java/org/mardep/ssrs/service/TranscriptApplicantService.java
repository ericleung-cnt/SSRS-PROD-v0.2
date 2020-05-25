package org.mardep.ssrs.service;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrTranscriptApplicantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TranscriptApplicantService extends AbstractService implements ITranscriptApplicantService  {

		@Autowired
		IOcrTranscriptApplicantDao transcriptApplicantDao;
		
		//@Override
		//public List<TranscriptApplicant>
}
