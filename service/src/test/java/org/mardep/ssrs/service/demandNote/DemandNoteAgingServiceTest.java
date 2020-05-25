package org.mardep.ssrs.service.demandNote;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.domain.dn.DemandNoteAging;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.service.DemandNoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class DemandNoteAgingServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void testPrepareAgingRecords() throws ParseException {
		List<DemandNoteHeader> dnHeaders = new ArrayList<DemandNoteHeader>();
		
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
		DemandNoteHeader h1 = new DemandNoteHeader();
		h1.setDemandNoteNo("001");
		h1.setGenerationTime(sdFormat.parse("2019-01-01"));
		h1.setDueDate(sdFormat.parse("2019-01-01"));
		h1.setPaymentStatus("outstanding");
		dnHeaders.add(h1);
		
		DemandNoteHeader h2 = new DemandNoteHeader();
		h2.setDemandNoteNo("002");
		h2.setGenerationTime(sdFormat.parse("2019-01-01"));
		h2.setDueDate(sdFormat.parse("2019-04-01"));
		h2.setPaymentStatus("outstanding");
		dnHeaders.add(h2);

		DemandNoteHeader h3 = new DemandNoteHeader();
		h3.setDemandNoteNo("003");
		h3.setGenerationTime(sdFormat.parse("2019-01-01"));
		h3.setDueDate(sdFormat.parse("2019-07-01"));
		h3.setPaymentStatus("outstanding");
		dnHeaders.add(h3);

		DemandNoteHeader h4 = new DemandNoteHeader();
		h4.setDemandNoteNo("004");
		h4.setGenerationTime(sdFormat.parse("2019-01-01"));
		h4.setDueDate(sdFormat.parse("2019-10-01"));
		h4.setPaymentStatus("outstanding");
		dnHeaders.add(h4);

		DemandNoteService svc = new DemandNoteService();
		List<DemandNoteAging> agings = svc.prepareAgingRecords(dnHeaders);
		
		
	}

}
