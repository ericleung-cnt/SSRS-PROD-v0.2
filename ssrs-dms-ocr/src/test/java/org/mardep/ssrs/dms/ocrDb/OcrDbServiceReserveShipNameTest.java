package org.mardep.ssrs.dms.ocrDb;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.dbService.IOcrDbServiceReserveShipName;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceReserveShipName;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlReserveShipName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrDbServiceReserveShipNameTest {
	@Autowired
	IOcrBaseService baseService;

	@Autowired
	IOcrServiceReserveShipName ocrService;

	@Autowired
	IOcrDbServiceReserveShipName dbService;

	@Test
	public void testGetEntityFromOcrAndSave() {
		List<String> filenames = baseService.getXmlFileList("XmlTemplatePath_ReserveShipName");
		for (String name : filenames) {
			OcrXmlReserveShipName xml = ocrService.getEntityFromOcr(name);
			//transcriptService.saveEntityToSSRS(entity);
			try {
				if (!name.endsWith(".xml")) {
					throw new IllegalArgumentException("invalid filename " + name);
				}
				try (FileInputStream fis = new FileInputStream(name.substring(0, name.length() - 4) + ".PDF")) {
					byte[] pdf = IOUtils.toByteArray(fis);
					dbService.save(xml, pdf);
				}
				//System.out.println("id: " + entity.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


}
