package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Service;

@Service("RPT_SR_022")
public class RPT_SR_022 extends AbstractSrReport {

	public RPT_SR_022() {
		super("RPT-SR-022-Summary.jrxml", null);
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		logger.info("Report Date:{}", reportDate);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reportDate", createDateFormat().format(reportDate));
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		params.put("userId", currentUser);
		;
		String thisYr = new SimpleDateFormat("yyyy").format(reportDate);
		String lastYrDate = "31-DEC-"+ (Integer.parseInt(thisYr) - 1);
		params.put("lastYearDate", lastYrDate);
		params.put("thisYear", thisYr);
		List<Map<String, Object>> rows = rmDao.getShipRegAnnualReport(reportDate);
		return jasperReportService.generateReport(getReportFileName(), rows, params);
	}
}
