package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.text.ParseException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlTranscript;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;

public interface IOcrDbServiceTranscript {

	void save(OcrXmlTranscript xml, byte[] pdf) throws IOException, ParseException;

}
