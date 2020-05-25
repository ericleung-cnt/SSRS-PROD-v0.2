package org.mardep.ssrs.dms.ocr;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceCos;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrServiceCosTest {
//	@Autowired 
//	OcrService service;
	

	@Autowired
	IOcrBaseService ocrService;
	
	@Autowired
	IOcrServiceCos cosService;
	
//	@Test
//	public void testGetFileList() {
//		List<String> filenames = ocrService.getXmlFileList("c:/DMS_Template/COS");
//		for (String name : filenames) {
//			System.out.println(name);
//		}
//	}
//	
	@Test
	public void testGetEntityFromOcr() {
		//List<String> filenames = ocrService.getXmlFileList("c:/DMS_Template/COS");
		List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_Cos");
		for (String name : filenames) {
			OcrXmlCos entity = cosService.getEntityFromOcr(name);
			cosService.saveEntityToSSRS(entity);
		}		
	}
	
//	@Test
//	public void testArchiveEntity() {
//		Service.archiveEntity("Data_00000001.xml", "Data_processed_00000001.xml", "C:/DMS_Template/COS/");
//	}
}
