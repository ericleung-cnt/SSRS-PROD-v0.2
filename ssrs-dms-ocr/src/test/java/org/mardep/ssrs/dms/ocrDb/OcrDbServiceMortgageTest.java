package org.mardep.ssrs.dms.ocrDb;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceRegisterMortgage;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceRegisterMortgage;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlRegisterMortgage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrDbServiceMortgageTest {

	@Autowired
	IOcrBaseService baseService;

	@Autowired
	IOcrServiceRegisterMortgage ocrService;

	@Autowired
	IOcrDbServiceRegisterMortgage dbService;

	@Test
	public void testRegisterMortgage_GetEntityFromOcrAndSave() {
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_RegisterMortgage");
		for (String name : filenames) {
			OcrXmlRegisterMortgage xml = ocrService.getEntityFromOcr(name);
			try {
				if (!name.endsWith(".xml")) {
					throw new IllegalArgumentException("invalid filename " + name);
				}
				try (FileInputStream fis = new FileInputStream(name.substring(0, name.length() - 4) + ".PDF")) {
					byte[] pdf = IOUtils.toByteArray(fis);
					dbService.save(xml, pdf);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
