package org.mardep.ssrs.dms.ocrDb;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceCos;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceCos;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrDbServiceCosTest {

	@Autowired
	IOcrBaseService baseService;

	@Autowired
	IOcrServiceCos ocrService;

	@Autowired
	IOcrDbServiceCos dbService;

	@Test
	public void testCos_GetEntityFromOcrAndSave() {
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_Cos");
		for (String name : filenames) {
			OcrXmlCos xml = ocrService.getEntityFromOcr(name);
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
