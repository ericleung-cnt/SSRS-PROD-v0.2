package org.mardep.ssrs.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.domain.codetable.DocumentCheckList;
import org.mardep.ssrs.domain.codetable.DocumentRemark;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.service.ICodeTableService;
import org.mardep.ssrs.service.IDocRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class AcdReportTest {
	
	@Autowired
	@Qualifier("RPT_NewRegAcd")
	IReportGenerator RPT_NewRegAcd;

	@Autowired
	@Qualifier("RPT_ProToFullAcd")
	IReportGenerator RPT_ProToFullAcd;
	
	@Autowired
	IDocRemarkService remarkSvc;

	@Autowired
	ICodeTableService codeTableService;
	
	@Before
	public void init() throws Exception {
		User user = new User();
		user.setId("TESTER");
		UserContextThreadLocalHolder.setCurrentUser(user);
		
	}
	
	@Test
	@Transactional()
	@Rollback(false)
	public void testService() throws Exception {
		Map<String, Object> inputParam = new HashMap<>();
		List<String> docList = new ArrayList<String>();
		Map<String, Criteria> map = new HashMap<String, Criteria>();
		map.put("type", new Criteria("type", "Deletion"));
		List<DocumentCheckList> docCheckListList = new ArrayList<DocumentCheckList>();
		codeTableService.findByPaging(map, docCheckListList, DocumentCheckList.class);
		for(DocumentCheckList d:docCheckListList){
			docList.add(d.getDesc()+SystemUtils.LINE_SEPARATOR+d.getDescChi());
		}

		List<DocumentRemark> remarkList = remarkSvc.getByGroup("ACD");
		List<String> rList = new ArrayList<String>();
		for(DocumentRemark d:remarkList){
			rList.add(d.getRemark());
		}
		
		inputParam.put("shipName", "COSHONOUR LAKE");
		inputParam.put("companyName", "COSHONOUR LAKE MARITIME LIMITED");
		inputParam.put("docList", docList);
		inputParam.put("remarkList", rList);
		writePdf(RPT_NewRegAcd.generate(inputParam), "NewReg");

		inputParam = new HashMap<>();
		inputParam.put("shipName", "SITC QINZHOU");
		inputParam.put("companyName", "SITC LAEM CHABANG SHIPPING COMPANY LIMITED");
		inputParam.put("docList", docList);
		inputParam.put("remarkList", rList);
		writePdf(RPT_ProToFullAcd.generate(inputParam), "ProToFull");
	}
	
	private void writePdf(byte[] pdf, String prefix) throws IOException, FileNotFoundException {
		File tempFile = File.createTempFile(prefix, ".pdf");
//		tempFile.deleteOnExit();
		try (FileOutputStream fos =  new FileOutputStream(tempFile)) {
			fos.write(pdf);
		}
		System.out.println(tempFile.getAbsolutePath() + " is written");
	}

}
