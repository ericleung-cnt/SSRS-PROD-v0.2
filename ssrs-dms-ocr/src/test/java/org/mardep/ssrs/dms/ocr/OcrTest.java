package org.mardep.ssrs.dms.ocr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceCos;
import org.mardep.ssrs.dms.ocr.service.OcrBaseService;
import org.mardep.ssrs.service.IShipRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrTest {
		
	@Autowired 
	OcrBaseService service;
	
	@Autowired
	IOcrServiceCos cosService;
	
	@Autowired
	IShipRegService srService;
	
//	@Test 
//	public void testSomething() {
//		service.getClass(); 
//		String officialNo = service.getOfficialNo();
//		System.out.println("official no: " + officialNo);
//	}
//	 
//
	@Test
	public void testOfficialNo() {
		String officialNo = srService.getOffNo();
		System.out.println("official no: " + officialNo);
	}
	
}
