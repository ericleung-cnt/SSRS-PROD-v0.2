package org.mardep.ssrs.dms.ocr.action;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceTranscript;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceTranscript;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlTranscript;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OcrActionServiceTranscript implements IOcrActionServiceTranscript {

	@Autowired
	IOcrBaseService baseService;

	@Autowired
	IOcrServiceTranscript transcriptService;

	@Autowired
	IOcrDbServiceTranscript dbService;

	@Override
	public void getEntityFromOcrAndSave() {
		// TODO Auto-generated method stub
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_Transcript");
		for (String name : filenames) {
			OcrXmlTranscript xml = transcriptService.getEntityFromOcr(name);
			if (xml==null) {
				baseService.postActionInvalidEntity(name);
				continue;
			}
			//transcriptService.saveEntityToSSRS(entity);
			try {
				if (!name.endsWith(".xml")) {
					throw new IllegalArgumentException("invalid filename " + name);
				}
				try (FileInputStream fis = new FileInputStream(name.substring(0, name.length() - 4) + ".PDF")) {
					byte[] pdf = IOUtils.toByteArray(fis);
					//OcrEntityTranscript entity = dbService.save(xml, pdf);
					//System.out.println("id: " + entity.getId());
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
