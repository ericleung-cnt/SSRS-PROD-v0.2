package org.mardep.ssrs.dms.ocr;

import java.text.ParseException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceCompanySearch;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCompanySearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrServiceCompanySearchTest {
	@Autowired
	IOcrBaseService ocrService;
	
	@Autowired
	IOcrServiceCompanySearch service;
	
	@Test
	public void testGetEntityFromOcr() throws ParseException {
//		Properties props = new Properties();
//		InputStream input = null;
//		try {		
//			input = getClass().getResourceAsStream("/ocr.properties");
//			props.load(input);
//			List<String> filenames = ocrService.getXmlFileList("c:/DMS_Template/CompanySearch");
			List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_CompanySearch");
			for (String name : filenames) {
				OcrXmlCompanySearch entity = service.getEntityFromOcr(name);
				service.saveEntityToSSRS(entity);
			}	
//		} catch (IOException ex) {
//	        ex.printStackTrace();
//	    } finally {
//	        if (input != null) {
//	            try {
//	                input.close();
//	            } catch (IOException e) {
//	                e.printStackTrace();
//	            }
//	        }
//	    }	
	}
}
