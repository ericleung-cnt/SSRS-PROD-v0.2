package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlRegisterMortgage;

public interface IOcrDbServiceRegisterMortgage {
	public void save(OcrXmlRegisterMortgage xml, byte[] pdf) throws IOException;
}
