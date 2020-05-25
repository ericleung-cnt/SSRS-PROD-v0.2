package org.mardep.ssrs.dms.ocr.action;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceReserveShipName;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceReserveShipName;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlReserveShipName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OcrActionServiceReserveShipName implements IOcrActionServiceReserveShipName {

	@Autowired
	IOcrBaseService baseService;

	@Autowired
	IOcrServiceReserveShipName ocrService;

	@Autowired
	IOcrDbServiceReserveShipName dbService;

	@Override
	public void getEntityFromOcrAndSave() {
		// TODO Auto-generated method stub
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_ReserveShipName");
		for (String name : filenames) {
			OcrXmlReserveShipName xml = ocrService.getEntityFromOcr(name);
			//transcriptService.saveEntityToSSRS(entity);
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
				//System.out.println("id: " + entity.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
				baseService.postActionExceptionEntity(name);
			}
		}
	}

}
