package org.mardep.ssrs.dms.ocr.service;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlCsrInitial;

public interface IOcrServiceCsrInitial {
	public OcrXmlCsrInitial getEntityFromOcr(String xmlFile);
	public void uploadToDMS(String imageFile, OcrXmlCsrInitial entity);
	public void saveEntityToSSRS(OcrXmlCsrInitial entity);
}
