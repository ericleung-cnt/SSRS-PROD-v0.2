package org.mardep.ssrs.service.test;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.report.IReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class ReportServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("RPT_MMO_001")
	IReportGenerator mmo001;

	@Autowired
	IRegMasterDao rmDao;
	@Test
	public void testRptMmo001() throws Exception {
		rmDao.getOwnershipReport(new Date());

		Map<String, Object> inputParam = new HashMap<String, Object>();
		inputParam.put(Cons.SERB_NO, "TEST");
		inputParam.put(Cons.SEAFARER_ID, "1234567");
		byte[] b = mmo001.generate(inputParam);
		String fileName = SystemUtils.USER_HOME+"/temp/RptMmo001_"+System.currentTimeMillis()+".pdf";
		File fileToWriteTo = new File(fileName);
	    FileUtils.writeByteArrayToFile(fileToWriteTo, b);
	}

}
