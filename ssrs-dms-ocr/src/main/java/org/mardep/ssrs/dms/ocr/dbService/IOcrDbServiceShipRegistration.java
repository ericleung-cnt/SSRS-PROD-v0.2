package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.text.ParseException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlShipRegistration;

public interface IOcrDbServiceShipRegistration {
	public void save(OcrXmlShipRegistration xml, byte[] pdf) throws IOException, ParseException;
}
