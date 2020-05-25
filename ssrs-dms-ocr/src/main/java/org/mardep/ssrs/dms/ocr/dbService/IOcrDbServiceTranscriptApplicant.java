package org.mardep.ssrs.dms.ocr.dbService;

import org.mardep.ssrs.domain.ocr.OcrEntityTranscriptApplicant;

public interface IOcrDbServiceTranscriptApplicant {
	void save(OcrEntityTranscriptApplicant entity);
	void saveIfNotExist(OcrEntityTranscriptApplicant entity);
}
