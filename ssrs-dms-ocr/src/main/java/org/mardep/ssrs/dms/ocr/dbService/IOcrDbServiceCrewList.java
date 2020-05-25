package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.text.ParseException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2A;

public interface IOcrDbServiceCrewList {
	public void save(OcrXmlEng2 xml, byte[] pdf) throws IOException, ParseException;
	public void save(OcrXmlEng2A xml, byte[] pdf) throws IOException, ParseException;
}
