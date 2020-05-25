package org.mardep.ssrs.dms.ocr.service;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlCos;

public interface IOcrServiceCos {
	public OcrXmlCos getEntityFromOcr(String xmlFile);
	public void uploadToDMS(String imageFile, OcrXmlCos entity);
	public void saveEntityToSSRS(OcrXmlCos entity);
}
