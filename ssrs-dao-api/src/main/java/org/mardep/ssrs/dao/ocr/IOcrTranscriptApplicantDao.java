package org.mardep.ssrs.dao.ocr;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscriptApplicant;

public interface IOcrTranscriptApplicantDao extends IBaseDao<OcrEntityTranscriptApplicant, Integer>{
	void saveIfNotExist(OcrEntityTranscriptApplicant entity);
}
