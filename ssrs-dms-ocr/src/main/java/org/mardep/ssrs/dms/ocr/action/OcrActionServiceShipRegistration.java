package org.mardep.ssrs.dms.ocr.action;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceShipRegistration;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceShipRegistration;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlShipRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OcrActionServiceShipRegistration implements IOcrActionServiceShipRegistration {

	@Autowired
	IOcrBaseService baseService;

	@Autowired
	IOcrServiceShipRegistration ocrService;

	@Autowired
	IOcrDbServiceShipRegistration dbService;

	@Override
	public void getEntityFromOcrAndSave() {
		// TODO Auto-generated method stub
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_ShipRegistration");
		for (String name : filenames) {
			OcrXmlShipRegistration xml = ocrService.getEntityFromOcr(name);
			if (xml==null) {
				baseService.postActionInvalidEntity(name);
				continue;
			}
			try {
				if (!name.endsWith(".xml")) {
					throw new IllegalArgumentException("invalid filename " + name);
				}
				try (FileInputStream fis = new FileInputStream(name.substring(0, name.length() - 4) + ".PDF")) {
					byte[] pdf = IOUtils.toByteArray(fis);
					dbService.save(xml, pdf);
				}
				baseService.postActionProcessedEntity(name);
			} catch (Exception ex) {
				ex.printStackTrace();
				baseService.postActionExceptionEntity(name);
			}
		}
	}

}
