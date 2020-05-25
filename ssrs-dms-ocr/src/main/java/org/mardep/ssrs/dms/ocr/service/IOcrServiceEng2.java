package org.mardep.ssrs.dms.ocr.service;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2;

public interface IOcrServiceEng2 {
	public OcrXmlEng2 getEntityFromOcr(String xmlFile);
	public void uploadToDMS(String imageFile, OcrXmlEng2 entity);
	public void saveEntityToSSRS(OcrXmlEng2 entity);
}
