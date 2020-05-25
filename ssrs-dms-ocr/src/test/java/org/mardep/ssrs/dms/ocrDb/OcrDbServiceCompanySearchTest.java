package org.mardep.ssrs.dms.ocrDb;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceCompanySearch;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceCompanySearch;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCompanySearch;
import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrDbServiceCompanySearchTest {

	@Autowired
	IOcrBaseService ocrService;

	@Autowired
	IOcrServiceCompanySearch companySearchService;

	@Autowired
	IOcrDbServiceCompanySearch dbService;

	@Test
	public void testGetEntityFromOcrAndSave() {
		List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_CompanySearch");
		for (String name : filenames) {
			OcrXmlCompanySearch xml = companySearchService.getEntityFromOcr(name);
			//transcriptService.saveEntityToSSRS(entity);
			try {
				if (!name.endsWith(".xml")) {
					throw new IllegalArgumentException("invalid filename " + name);
				}
				try (FileInputStream fis = new FileInputStream(name.substring(0, name.length() - 4) + ".PDF")) {
					byte[] pdf = IOUtils.toByteArray(fis);
					OcrEntityCompanySearch entity = dbService.save(xml, pdf);
					System.out.println("id: " + entity.getId());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
