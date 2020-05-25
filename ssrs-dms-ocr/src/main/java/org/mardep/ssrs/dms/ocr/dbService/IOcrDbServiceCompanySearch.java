package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.text.ParseException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlCompanySearch;
import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;

public interface IOcrDbServiceCompanySearch {
	OcrEntityCompanySearch save(OcrXmlCompanySearch xml, byte[] pdf) throws IOException, ParseException;
}
