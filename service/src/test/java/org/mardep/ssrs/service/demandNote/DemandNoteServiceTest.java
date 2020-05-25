package org.mardep.ssrs.service.demandNote;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.service.DemandNoteAtcService;
import org.mardep.ssrs.service.IDemandNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
//@ContextConfiguration("/test-context.xml")
@Transactional
public class DemandNoteServiceTest {

//	@Autowired
//	IDemandNoteService dnSvc;
	
	// 2016 paid full, 2017 pay full
	// last ATC is half
	@Test
	public void testATC_RegDate_NoDetain_Passed1Yrs_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2017");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = halfATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);
	}
	
	// 2016 paid full, 2017 pay full
	// last ATC will not impact ATC when reg passed 1 yr
	@Test
	public void testATC_RegDate_NoDetain_Passed1Yrs_NoReturnHalfATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2017");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = new BigDecimal(100.00);
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);
		//assertFalse(expectATC == halfATC);
	}

	// 2016 paid full, 2017 paid full, 2018 pay half
	// last ATC is half
	@Test
	public void testATC_RegDate_NoDetain_Passed2Yrs_ReturnHalfATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2018");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, halfATC);
	}

	// 2016 paid full, 2017 paid full, 2018 pay half, 2019 paid full
	// last ATC is half
	@Test
	public void testATC_RegDate_NoDetain_Passed3Yrs_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2019");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = halfATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);
	}
	
	// 2016 paid full, 2017 paid full, 2018 pay half, 2019 paid full, 2020 pay half
	// last ATC is half
	@Test
	public void testATC_RegDate_NoDetain_Passed4Yrs_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2020");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, halfATC);
	}

	// last ATC is full
	@Test
	public void testATC_RegDate_NoDetain_Passed3Yrs_ReturnHalfATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2019");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, halfATC);
	}
	
//	// 2017 paid full, 2018 paid full, 2019 pay full
//	// passed 2 yrs after reg date, last ATC = full
//	@Test
//	public void testATC_RegDate_Detain_Passed2Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date currentDate = sdf.parse("13/06/2019");
//		Date regDate = sdf.parse("13/06/2016");
//		Date detainDate = sdf.parse("14/06/2017");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, currentDate, fullATC, halfATC);
//		assertEquals(expectATC, fullATC);		
//	}
//	
//	// 2017 paid full, 2018 paid full, 2019 pay full
//	// passed 2 yrs after reg date, last ATC = full
//	@Test
//	public void testATC_RegDate_Detain_Passed2Yrs_ReturnHalfATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date dueDate = sdf.parse("13/06/2019");
//		Date regDate = sdf.parse("13/06/2016");
//		Date detainDate = sdf.parse("13/06/2017");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = fullATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertEquals(expectATC, halfATC);				
//	}
	
	// within 2 yrs after reg date, last ATC = full
	// last ATC is fullATC
	@Test
	public void testATC_RegDate_NoDetain_Within2Yrs_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2019");
		Date regDate = sdf.parse("13/06/2018");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);					
	}
	
	// within 2 yrs after reg date, last ATC = full
	// last ATC is fullATC
	@Test
	public void testATC_RegDate_NoDetain_Within2Yrs_NotReturnHalfATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2019");
		Date regDate = sdf.parse("13/06/2018");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		//assertEquals(expectATC, lastATC);			
		assertFalse(expectATC == halfATC);
	}
	
//	@Test
//	public void testATC_DetainDate_RegDate_Within2Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date currentDate = sdf.parse("20/11/2019");
//		Date regDate = sdf.parse("13/06/2018");
//		Date detainDate = sdf.parse("13/06/2018");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, currentDate, fullATC, halfATC);
//		assertTrue(expectATC == fullATC);					
//	}
	
//	@Test
//	public void testATC_DetainDate_RegDate_Within2Yrs_NotReturnHalfATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date currentDate = sdf.parse("20/11/2019");
//		Date regDate = sdf.parse("13/06/2018");
//		Date detainDate = sdf.parse("13/06/2018");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, currentDate, fullATC, fullATC);
//		assertFalse(expectATC == halfATC);						
//	}
	
//	@Test
//	public void testATC_DetainDate_BeforeAnniversaryDate_Within2Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date currentDate = sdf.parse("20/11/2019");
//		Date regDate = sdf.parse("13/06/2018");
//		Date detainDate = sdf.parse("01/06/2018");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, currentDate, fullATC, fullATC);
//		assertTrue(expectATC == fullATC);							
//	}
	
//	@Test
//	public void testATC_DetainDate_AfterAnniversaryDate_Within2Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date currentDate = sdf.parse("20/11/2019");
//		Date regDate = sdf.parse("13/06/2018");
//		Date detainDate = sdf.parse("01/07/2018");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, currentDate, fullATC, fullATC);
//		assertTrue(expectATC == fullATC);									
//	}

//	@Test
//	public void testATC_DetainDate_AfterAnniversaryDate_After2Yrs_ReturnHalfATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		//Date currentDate = sdf.parse("20/11/2019");
//		Date currentDate = sdf.parse("13/06/2019");
//		Date regDate = sdf.parse("13/06/2016");
//		Date detainDate = sdf.parse("01/07/2017");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, currentDate, fullATC, fullATC);
//		assertTrue(expectATC.equals(halfATC));									
//	}

	// 2018 paid full, 2019 paid full
	@Test
	public void testATC_DetainDate_AfterAnniversaryDate_Within2Yrs_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		//Date currentDate = sdf.parse("20/11/2019");
		Date dueDate = sdf.parse("13/06/2019");
		Date regDate = sdf.parse("13/06/2016");	
		Date detainDate = sdf.parse("01/07/2017");
		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
				
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertTrue(expectATC.equals(fullATC));									
	}

	// 2017 paid full, 2018 paid full, 2019 pay half
	@Test
	public void testATC_DetainDate_AfterAnniversaryDate_Equal2Yrs_ReturnHalfATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		//Date currentDate = sdf.parse("20/11/2019");
		Date dueDate = sdf.parse("13/06/2019");
		Date regDate = sdf.parse("13/06/2016");	
		Date detainDate = sdf.parse("13/06/2017");
		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertTrue(expectATC.equals(halfATC));									
	}

	// 2018 paid full, 2019 paid full, 2020 pay half
	@Test
	public void testATC_DetainDate_AfterAnniversaryDate_JustAfter2Yrs_ReturnHalfATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		//Date currentDate = sdf.parse("20/11/2019");
		Date dueDate = sdf.parse("13/06/2020");
		Date regDate = sdf.parse("13/06/2017"); 	
		Date detainDate = sdf.parse("01/07/2017");
		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertTrue(expectATC.equals(halfATC));									
	}

	// 2018 paid full, 2019 paid full, 2020 pay half, 2021 pay full, 2022 pay half
	// last ATC is full
	@Test
	public void testATC_DetainDate_AfterAnniversaryDate_FarAfter2Yrs_ReturnHalfATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		//Date currentDate = sdf.parse("20/11/2019");
		Date dueDate = sdf.parse("13/06/2021");
		Date regDate = sdf.parse("13/06/2017");	 
		Date detainDate = sdf.parse("01/07/2017");
		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertTrue(expectATC.equals(halfATC));									
	}

	// 2018 paid full, 2019 paid full, 2020 pay half, 2021 pay full
	// last ATC is half
	@Test
	public void testATC_DetainDate_AfterAnniversaryDate_FarAfter2Yrs_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		//Date currentDate = sdf.parse("20/11/2019");
		Date dueDate = sdf.parse("13/06/2021");
		Date regDate = sdf.parse("13/06/2017");
		Date detainDate = sdf.parse("01/07/2017");
		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = halfATC;

		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertTrue(expectATC.equals(fullATC));									
	}


}
