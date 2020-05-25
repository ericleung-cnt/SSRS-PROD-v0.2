package org.mardep.ssrs.dms.ocrDb;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceCrewList;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceEng2;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceEng2A;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrDbServiceCrewListTest {

	@Autowired
	IOcrBaseService ocrService;

	@Autowired
	IOcrServiceEng2 eng2Service;

	@Autowired
	IOcrServiceEng2A eng2AService;

	@Autowired
	IOcrDbServiceCrewList crewListDbService;

	@Test
	public void testEng2_GetEntityFromOcrAndSave() {
		List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_Eng2");
		for (String name : filenames) {
			OcrXmlEng2 xml = eng2Service.getEntityFromOcr(name);
			try {
				crewListDbService.save(xml, new byte[0]);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Test
	public void testEng2A_GetEntityFromOcrAndSave() {
		List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_Eng2A");
		for (String name : filenames) {
			OcrXmlEng2A xml = eng2AService.getEntityFromOcr(name);
			try {
				if (!name.endsWith(".xml")) {
					throw new IllegalArgumentException("invalid filename " + name);
				}
				try (FileInputStream fis = new FileInputStream(name.substring(0, name.length() - 4) + ".PDF")) {
					byte[] pdf = IOUtils.toByteArray(fis);
					crewListDbService.save(xml, pdf);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
