package org.mardep.ssrs.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.service.IJasperReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class FinReportTest {
	 
	private final static Logger logger = LoggerFactory.getLogger(FinReportTest.class);
	
	@Autowired
	@Qualifier("RPT_FIN_006")
	IReportGenerator fin006; 

	@Autowired
	@Qualifier("RPT_FIN_REFUND")
	IReportGenerator finRefund; 
	
	@Autowired
	@Qualifier("RPT_FIN_AGING")
	IReportGenerator finAging; 
	
	@Autowired
	IJasperReportService jasper;
	
	@Before
	public void init() {
		User user = new User();
		user.setId("TESTER");
		UserContextThreadLocalHolder.setCurrentUser(user);
	}
	
	@Test
	@Transactional()
	@Rollback(false)
	public void testFinReport006Service() throws Exception {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put("reportDate", new Date());
		inputParam.put("dateFrom", DateUtils.parseDate("01/01/2019", "MM/dd/yyyy"));
		inputParam.put("dateTo", DateUtils.parseDate("12/30/2019", "MM/dd/yyyy"));
		
		writePdf(fin006.generate(inputParam), "ReceiptCollectedReport");
	}

	@Test
	@Transactional()
	@Rollback(false)
	public void testFinReportRefundService() throws Exception {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put("reportDate", new Date());
		inputParam.put("dateFrom", DateUtils.parseDate("01/01/2019", "MM/dd/yyyy"));
		inputParam.put("dateTo", DateUtils.parseDate("12/30/2019", "MM/dd/yyyy"));
		
		writePdf(finRefund.generate(inputParam), "Refund Report");
	}

	@Test
	@Transactional()
	@Rollback(false)
	public void testFinReportAgingService() throws Exception {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put("reportDate", new Date());
		inputParam.put("dateFrom", DateUtils.parseDate("01/01/2019", "MM/dd/yyyy"));
		inputParam.put("dateTo", DateUtils.parseDate("06/30/2019", "MM/dd/yyyy"));
		
		writePdf(finAging.generate(inputParam), "Aging Report");
	}
	
	private void writePdf(byte[] pdf, String prefix) throws IOException, FileNotFoundException {
		File tempFile = File.createTempFile(prefix, ".pdf");
		tempFile.deleteOnExit();
		try (FileOutputStream fos =  new FileOutputStream(tempFile)) {
			fos.write(pdf);
		}
		logger.info(tempFile.getAbsolutePath() + " is written");
	}

}
