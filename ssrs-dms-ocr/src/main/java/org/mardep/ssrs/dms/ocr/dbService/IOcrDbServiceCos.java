package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlCos;

public interface IOcrDbServiceCos {
	public void save(OcrXmlCos xml, byte[] pdf) throws IOException;
}
