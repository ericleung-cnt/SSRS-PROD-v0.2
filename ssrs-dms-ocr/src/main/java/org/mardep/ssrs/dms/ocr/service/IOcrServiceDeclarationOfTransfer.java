package org.mardep.ssrs.dms.ocr.service;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlDeclarationOfTransfer;

public interface IOcrServiceDeclarationOfTransfer {
	public OcrXmlDeclarationOfTransfer getEntityFromOcr(String xmlFile);
	public void uploadToDMS(String imageFile, OcrXmlDeclarationOfTransfer entity);
	public void saveEntityToSSRS(OcrXmlDeclarationOfTransfer entity);
}
