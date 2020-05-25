package org.mardep.ssrs.dms.ocr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrServiceTest {
	@Autowired
	IOcrBaseService ocrService;

	@Test
	public void testPostActionProcessedEntity() {
		ocrService.postActionProcessedEntity("Data_00000001.xml");
	}

	@Test
	public void testPostActionInvalidEntity() {
		ocrService.postActionInvalidEntity("Data_00000001.xml");
	}
	@Test
	public void testPostActionExceptionEntity() {
		ocrService.postActionExceptionEntity("Data_00000001.xml");
	}

}
