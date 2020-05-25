package org.mardep.ssrs.dms.ocr;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceRegisterMortgage;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlRegisterMortgage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrServiceRegisterMortgageTest {
	@Autowired
	IOcrBaseService ocrService;
	
	@Autowired
	IOcrServiceRegisterMortgage service;
	
	@Test
	public void testGetEntityFromOcr() {
		List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_RegisterMortgage");
		for (String name : filenames) {
			OcrXmlRegisterMortgage entity = service.getEntityFromOcr(name);
			service.saveEntityToSSRS(entity);
		}		
	}
}
