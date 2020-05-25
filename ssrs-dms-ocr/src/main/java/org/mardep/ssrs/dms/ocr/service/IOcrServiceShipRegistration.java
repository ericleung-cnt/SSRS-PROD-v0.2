package org.mardep.ssrs.dms.ocr.service;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlShipRegistration;

public interface IOcrServiceShipRegistration {
	public OcrXmlShipRegistration getEntityFromOcr(String xmlFile);
	public void uploadToDMS(String imageFile, OcrXmlShipRegistration entity);
	public void saveEntityToSSRS(OcrXmlShipRegistration entity);

}
