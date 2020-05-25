package org.mardep.ssrs.dms.ocr;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceEng2;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrServiceEng2Test {

	@Autowired
	IOcrBaseService ocrService;
	
	@Autowired
	IOcrServiceEng2 service;
	
	@Test
	public void testGetEntityFromOcr() {
		List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_Eng2");
		for (String name : filenames) {
			OcrXmlEng2 entity = service.getEntityFromOcr(name);
			service.saveEntityToSSRS(entity);
		}		
	}
}
