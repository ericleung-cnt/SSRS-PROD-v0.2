package org.mardep.ssrs.dms.ocr.service;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlRegisterMortgage;

public interface IOcrServiceRegisterMortgage {
	public OcrXmlRegisterMortgage getEntityFromOcr(String xmlFile);
	public void uploadToDMS(String imageFile, OcrXmlRegisterMortgage entity);
	public void saveEntityToSSRS(OcrXmlRegisterMortgage entity);
}
