package org.mardep.ssrs.service;

import java.util.List;

import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;

public interface ITranscriptApplicationService {
	List<OcrEntityTranscript> getAll();
	OcrEntityTranscript getById(int id);
	void remove(int id);
	OcrEntityTranscript save(OcrEntityTranscript entity);
}
