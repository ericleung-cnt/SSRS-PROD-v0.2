package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.text.ParseException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlCsrForm2;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCsrInitial;

public interface IOcrDbServiceCsr {
	public void save(OcrXmlCsrInitial xml, byte[] pdf) throws IOException;
	public void save(OcrXmlCsrForm2 xml, byte[] pdf) throws IOException, ParseException;
}
