package org.mardep.ssrs.dms.ocr.service;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlCsrForm2;

public interface IOcrServiceCsrForm2 {
	public OcrXmlCsrForm2 getEntityFromOcr(String xmlFile);
	public void uploadToDMS(String imageFile, OcrXmlCsrForm2 entity);
	public void saveEntityToSSRS(OcrXmlCsrForm2 entity);
}
