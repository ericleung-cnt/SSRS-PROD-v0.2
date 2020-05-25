package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.text.ParseException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlReserveShipName;

public interface IOcrDbServiceReserveShipName {
	public void save(OcrXmlReserveShipName xml, byte[] pdf) throws IOException, ParseException;
}
