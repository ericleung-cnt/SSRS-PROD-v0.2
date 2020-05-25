package org.mardep.ssrs.dao.ocr;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;

public interface IOcrTranscriptDao extends IBaseDao<OcrEntityTranscript, Integer>{
	List<OcrEntityTranscript> getAll();
	void remove(int id);
}
