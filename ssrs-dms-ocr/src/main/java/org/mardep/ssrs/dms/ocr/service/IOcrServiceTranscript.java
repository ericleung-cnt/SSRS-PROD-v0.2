package org.mardep.ssrs.dms.ocr.service;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlTranscript;

public interface IOcrServiceTranscript {
	public OcrXmlTranscript getEntityFromOcr(String xmlFile);
	public void uploadToDMS(String imageFile, OcrXmlTranscript entity);
	public void saveEntityToSSRS(OcrXmlTranscript entity);
}
