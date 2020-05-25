package org.mardep.ssrs.report.generator;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.codetable.IRankDao;
import org.mardep.ssrs.dao.codetable.IShipTypeDao;
import org.mardep.ssrs.report.IReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * MMO Report-018: Average Monthly Wages by Rank/Rating by Nationality
 * 
 *
 */
@Service("RPT_MMO_018")
public class MMO_018 extends AbstractKeyValue implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	IShipTypeDao shipTypeDao;
	
	@Autowired
	IRankDao rankDao;
	
	@Override
	public String getReportFileName() {
		return "MMO_018.jrxml";
	}
	
	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDateFrom = (Date)inputParam.get("reportDateFrom");
		Date reportDateTo = (Date)inputParam.get("reportDateTo");
		Long nationalityId = (Long)inputParam.get("nationality");
		
		List reportList = rankDao.getAverageWagesByNationality(reportDateFrom, reportDateTo, nationalityId);
		return generate(reportList.size() > 1 ? reportList.subList(1, reportList.size()) : Collections.emptyList(), 
				reportList.size() > 0 ? (HashMap<String, Object>) reportList.get(0) : new HashMap<>());
	}
	
}
