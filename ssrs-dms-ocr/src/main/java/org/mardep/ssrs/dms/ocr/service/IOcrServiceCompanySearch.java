package org.mardep.ssrs.dms.ocr.service;

import java.text.ParseException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlCompanySearch;

public interface IOcrServiceCompanySearch {
	public OcrXmlCompanySearch getEntityFromOcr(String xmlFile);
	public void uploadToDMS(String imageFile, OcrXmlCompanySearch entity);
	public void saveEntityToSSRS(OcrXmlCompanySearch entity) throws ParseException;
}
