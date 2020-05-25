package org.mardep.ssrs.dms.ocrDb;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceShipRegistration;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceShipRegistration;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlShipRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrDbServiceShipRegistrationTest {
	@Autowired
	IOcrBaseService baseService;

	@Autowired
	IOcrServiceShipRegistration ocrService;

	@Autowired
	IOcrDbServiceShipRegistration dbService;

	@Test
	public void testShipRegistration_GetEntityFromOcrAndSave() {
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_ShipRegistration");
		for (String name : filenames) {
			OcrXmlShipRegistration xml = ocrService.getEntityFromOcr(name);
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
