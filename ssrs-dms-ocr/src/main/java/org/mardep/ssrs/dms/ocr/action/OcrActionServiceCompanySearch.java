package org.mardep.ssrs.dms.ocr.action;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceCompanySearch;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceCompanySearch;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCompanySearch;
import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OcrActionServiceCompanySearch implements IOcrActionServiceCompanySearch {

	@Autowired
	IOcrBaseService baseService;

	@Autowired
	IOcrServiceCompanySearch ocrService;

	@Autowired
	IOcrDbServiceCompanySearch dbService;

	@Override
	public void getEntityFromOcrAndSave() {
		// TODO Auto-generated method stub
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_CompanySearch");
		for (String name : filenames) {
			OcrXmlCompanySearch xml = ocrService.getEntityFromOcr(name);
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
					OcrEntityCompanySearch entity = dbService.save(xml, pdf);
					System.out.println("id: " + entity.getId());
				}
				baseService.postActionProcessedEntity(name);
			} catch (Exception ex) {
				ex.printStackTrace();
				baseService.postActionExceptionEntity(name);
			}
		}
	}

}
