package org.mardep.ssrs.dms.ocr;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceTranscript;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlTranscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrServiceTranscriptTest {
	
	@Autowired
	IOcrBaseService ocrService;
	
	@Autowired
	IOcrServiceTranscript transcriptService;
	
	@Test
	public void testGetEntityFromOcr() {
		List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_Transcript");
		for (String name : filenames) {
			OcrXmlTranscript entity = transcriptService.getEntityFromOcr(name);
			if (entity==null) {
				ocrService.postActionInvalidEntity(name);
			} else {
				transcriptService.saveEntityToSSRS(entity);
			}
		}		
	}
}
