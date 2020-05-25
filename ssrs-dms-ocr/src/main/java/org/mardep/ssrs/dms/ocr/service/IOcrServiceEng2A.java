package org.mardep.ssrs.dms.ocr.service;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2A;

public interface IOcrServiceEng2A {
	public OcrXmlEng2A getEntityFromOcr(String xmlFile);
	public void uploadToDMS(String imageFile, OcrXmlEng2A entity);
	public void saveEntityToSSRS(OcrXmlEng2A entity);
}
