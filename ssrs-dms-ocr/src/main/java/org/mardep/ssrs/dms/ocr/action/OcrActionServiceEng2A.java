package org.mardep.ssrs.dms.ocr.action;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceCrewList;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceEng2;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceEng2A;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OcrActionServiceEng2A implements IOcrActionServiceEng2A {

	@Autowired
	IOcrBaseService baseService;

	@Autowired
	IOcrServiceEng2 eng2Service;

	@Autowired
	IOcrServiceEng2A eng2AService;

	@Autowired
	IOcrDbServiceCrewList crewListDbService;

	@Override
	public void getEntityFromOcrAndSave() {
		// TODO Auto-generated method stub
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_Eng2A");
		for (String name : filenames) {
			OcrXmlEng2A xml = eng2AService.getEntityFromOcr(name);
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
					crewListDbService.save(xml, pdf);
				}
				baseService.postActionProcessedEntity(name);
			} catch (Exception ex) {
				ex.printStackTrace();
				baseService.postActionExceptionEntity(name);				
			}
		}
	}

}
