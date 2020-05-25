package org.mardep.ssrs.dms.ocr.action;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceDeclarationOfTransfer;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceDeclarationOfTransfer;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlDeclarationOfTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OcrActionServiceDeclarationOfTransfer implements IOcrActionServiceDeclarationOfTransfer {

	@Autowired
	IOcrBaseService baseService;
	
	@Autowired
	IOcrServiceDeclarationOfTransfer ocrService;
	
	@Autowired
	IOcrDbServiceDeclarationOfTransfer dbService;
	
	@Override
	public void getEntityFromOcrAndSave() {
		// TODO Auto-generated method stub
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_DeclarationOfTransfer");
		for (String name : filenames) {
			OcrXmlDeclarationOfTransfer xml = ocrService.getEntityFromOcr(name);
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
