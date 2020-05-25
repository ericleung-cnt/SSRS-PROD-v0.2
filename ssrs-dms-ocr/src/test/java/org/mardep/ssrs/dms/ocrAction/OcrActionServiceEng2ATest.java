package org.mardep.ssrs.dms.ocrAction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.action.IOcrActionServiceEng2A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrActionServiceEng2ATest {
	@Autowired
	IOcrActionServiceEng2A action;
	
	@Test
	public void testAction() {
		action.getEntityFromOcrAndSave();
	}
}
