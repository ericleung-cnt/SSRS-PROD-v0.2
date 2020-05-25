package org.mardep.ssrs.dms.ocrDb;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceTranscript;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceTranscript;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlTranscript;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrDbServiceTranscriptTest {
	@Autowired
	IOcrBaseService ocrService;

	@Autowired
	IOcrServiceTranscript transcriptService;

	@Autowired
	IOcrDbServiceTranscript dbService;

	@Test
	public void testGetEntityFromOcrAndSave() {
		List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_Transcript");
		for (String name : filenames) {
			OcrXmlTranscript xml = transcriptService.getEntityFromOcr(name);
			//transcriptService.saveEntityToSSRS(entity);
			try {
				if (!name.endsWith(".xml")) {
					throw new IllegalArgumentException("invalid filename " + name);
				}
				try (FileInputStream fis = new FileInputStream(name.substring(0, name.length() - 4) + ".PDF")) {
					byte[] pdf = IOUtils.toByteArray(fis);
					//OcrEntityTranscript entity = dbService.save(xml, pdf);
					//System.out.println("id: " + entity.getId());
					dbService.save(xml, pdf);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

//	@Test
//	public void testDateParse() {
//		try {
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date date1 = sdf.parse("20/03/2018");
//		System.out.println(date1);
//		} catch (ParseException ex) {
//			ex.printStackTrace();
//		}
//	}
}
