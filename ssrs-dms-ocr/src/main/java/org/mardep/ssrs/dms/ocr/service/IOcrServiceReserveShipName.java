package org.mardep.ssrs.dms.ocr.service;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlReserveShipName;

public interface IOcrServiceReserveShipName {
	public OcrXmlReserveShipName getEntityFromOcr(String xmlFile);
	public void uploadToDMS(String imageFile, OcrXmlReserveShipName entity);
	public void saveEntityToSSRS(OcrXmlReserveShipName entity);
}
