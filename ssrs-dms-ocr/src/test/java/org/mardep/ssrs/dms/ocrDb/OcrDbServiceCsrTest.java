package org.mardep.ssrs.dms.ocrDb;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceCsr;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceCsrForm2;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceCsrInitial;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCsrForm2;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCsrInitial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrDbServiceCsrTest {

	@Autowired
	IOcrBaseService baseService;

	@Autowired
	IOcrServiceCsrInitial ocrServiceCsrInitial;

	@Autowired
	IOcrServiceCsrForm2 ocrServiceCsrForm2;

	@Autowired
	IOcrDbServiceCsr dbService;

	@Test
	public void testCsrInitial_GetEntityFromOcrAndSave() {
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_CsrInitial");
		for (String name : filenames) {
			OcrXmlCsrInitial xml = ocrServiceCsrInitial.getEntityFromOcr(name);
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

	@Test
	public void testCsrForm2_GetEntityFromOcrAndSave() {
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_CsrForm2");
		for (String name : filenames) {
			OcrXmlCsrForm2 xml = ocrServiceCsrForm2.getEntityFromOcr(name);
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
