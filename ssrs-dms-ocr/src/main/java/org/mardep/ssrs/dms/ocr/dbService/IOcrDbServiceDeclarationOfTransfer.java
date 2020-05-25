package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlDeclarationOfTransfer;

public interface IOcrDbServiceDeclarationOfTransfer {
	public void save(OcrXmlDeclarationOfTransfer xml, byte[] pdf) throws IOException;
}
