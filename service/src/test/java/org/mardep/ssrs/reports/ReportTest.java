package org.mardep.ssrs.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.seafarer.ISeafarerDao;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.domain.seafarer.Seafarer;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.service.IJasperReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class ReportTest {
	@Autowired
	@Qualifier("RPT_SR_011")
	IReportGenerator rptSr011;
	@Autowired
	@Qualifier("RPT_MMO_008")
	IReportGenerator mmo008;
	@Autowired
	@Qualifier("RPT_MMO_004")
	IReportGenerator mmo004;
	@Autowired
	@Qualifier("RPT_MMO_001")
	IReportGenerator mmo001;
	@Autowired
	@Qualifier("RPT_MMO_007")
	IReportGenerator mmo007;
	@Autowired
	@Qualifier("RPT_MMO_009")
	IReportGenerator mmo009;
	@Autowired
	@Qualifier("RPT_MMO_005")
	IReportGenerator mmo005;
	@Autowired
	@Qualifier("RPT_MMO_011")
	IReportGenerator mmo011;

	@Autowired
	@Qualifier("RPT_SR_012")
	IReportGenerator sr012;
	@Autowired
	@Qualifier("RPT_MMO_010")
	IReportGenerator mmo010;
	@Autowired
	@Qualifier("RPT_MMO_003")
	IReportGenerator mmo003;
	@Autowired
	@Qualifier("RPT_MMO_002")
	IReportGenerator mmo002;
	@Autowired
	@Qualifier("RPT_SR_018")
	IReportGenerator sr018;
	@Autowired
	@Qualifier("RPT_MMO_006")
	IReportGenerator mmo006;
	@Autowired
	@Qualifier("RPT_SR_020")
	IReportGenerator sr020;
	@Autowired
	@Qualifier("RPT_SR_021")
	IReportGenerator sr021;
	@Autowired
	@Qualifier("RPT_SR_023")
	IReportGenerator sr023;
	@Autowired
	@Qualifier("RPT_SR_019")
	IReportGenerator sr019;
	@Autowired
	@Qualifier("RPT_SR_022")
	IReportGenerator sr022;

	@Autowired
	@Qualifier("RPT_SR_022_Detail")
	IReportGenerator sr022Detail;

	@Autowired
	ISeafarerDao seafarerDao;

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
	public void testService() throws Exception {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put("reportDate", new Date());
		inputParam.put("applNo", "2019/001");
		inputParam.put("nationalityId", 1L);
		inputParam.put("partType", "1");

		Seafarer seafarer = seafarerDao.findAll().get(0);
		String id = seafarer.getId();
		inputParam.put(Cons.SEAFARER_ID, id);
		inputParam.put(Cons.SERB_NO, seafarer.getSerbNo());
		inputParam.put("shipTypeCode", "CGO");
		
		inputParam.put("printCover", Boolean.TRUE);

		try (FileOutputStream fos = new FileOutputStream(System.currentTimeMillis() + ".pdf")) {
			fos.write(jasper.generateReport("PRG-SUPP-019_memoCor.jrxml", Arrays.asList(this), inputParam));
		}
		try {
			jasper.generateReport("RPT-SR-011.jrxml", inputParam, null, File.createTempFile("unittest", "").getAbsolutePath(), true);
			jasper.generateReport("RPT-SR-011.jrxml", inputParam, null, File.createTempFile("unittest", ".xls").getAbsolutePath(), true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("exception for jasper.generateReport");
		}

		writePdf(sr022Detail.generate(inputParam), "sr022Detail");
		writePdf(sr022.generate(inputParam), "sr022");
		writePdf(sr019.generate(inputParam), "sr019");
		rptSr011.generate(inputParam);

		mmo008.generate(inputParam);
		inputParam.put("partType", "1");
		mmo004.generate(inputParam);
		inputParam.put("partType", "2");
		mmo004.generate(inputParam);

		mmo001.generate(inputParam);
		mmo007.generate(inputParam);

		inputParam.put("rankingRating", "O");
		writePdf(mmo009.generate(inputParam), "m009O_");
		inputParam.put("rankingRating", "R");
		writePdf(mmo009.generate(inputParam), "m009R_");

		inputParam.put("partType", "1");
		mmo005.generate(inputParam);
		inputParam.put("partType", "2");
		mmo005.generate(inputParam);

		mmo011.generate(inputParam);

		inputParam.put("reportDateFrom", new Date(System.currentTimeMillis() - 365L * 24 * 3600));
		inputParam.put("reportDateTo", new Date());
		inputParam.put("reportFrom", inputParam.get("reportDateFrom"));
		inputParam.put("reportTo", inputParam.get("reportDateTo"));
		sr012.generate(inputParam);
		sr020.generate(inputParam);
		sr021.generate(inputParam);

		inputParam.put("rank", null);
		mmo010.generate(inputParam);
		inputParam.put("rank", 1L);
		mmo010.generate(inputParam);

		inputParam.put("partType", "1");
		mmo003.generate(inputParam);
		inputParam.put("partType", "2");
		mmo003.generate(inputParam);

		inputParam.put("partType", "1");
		mmo002.generate(inputParam);
		inputParam.put("partType", "2");
		mmo002.generate(inputParam);

		sr018.generate(inputParam);
		sr023.generate(inputParam);
		mmo006.generate(inputParam);
	}
	private void writePdf(byte[] pdf, String prefix) throws IOException, FileNotFoundException {
		File tempFile = File.createTempFile(prefix, ".pdf");
		tempFile.deleteOnExit();
		try (FileOutputStream fos =  new FileOutputStream(tempFile)) {
			fos.write(pdf);
		}
		System.out.println(tempFile.getAbsolutePath() + " is written");
	}

}
