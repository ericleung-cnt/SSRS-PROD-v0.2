package org.mardep.ssrs.report.generator;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.dao.sr.IApplDetailDao;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("DeregReasons")
public class DeregReasonsReport extends AbstractReportGenerator implements IReportGenerator {

	@Override
	public String getReportFileName() {
		return "DeRegReasons.jrxml";
	}
	
	@Autowired
	IApplDetailDao dao;

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date start = (Date) inputParam.get("startDate");
		Date end = (Date) inputParam.get("endDate");
		inputParam.put("userId", UserContextThreadLocalHolder.getCurrentUserId());
		List<Map<String, Object>> items = dao.deregReasonsReport(start, end);
		return super.generate(items, inputParam);
	}

}
