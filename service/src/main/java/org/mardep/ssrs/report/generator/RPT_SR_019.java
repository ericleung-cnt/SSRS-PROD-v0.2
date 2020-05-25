package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

@Service("RPT_SR_019")
public class RPT_SR_019 extends AbstractSrReport {

	public RPT_SR_019() {
		super("RPT-SR-019-Summary.jrxml", null);
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		Map<String, Object> params = rmDao.getPipelineSummary(reportDate);

		params.put("reportDate", createDateFormat().format(reportDate));
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		params.put("userId", currentUser);

		JasperReport jasperReport = jasperReportService.getJasperReport("RPT-SR-019-Detail.jrxml");
		params.put("SUBREPORT_1", jasperReport);
		List<Map<String, Object>> ds = rmDao.getPipelineDetailRows(reportDate);

		params.put("SUBREPORTDS_1", ds);

		List details = rmDao.getPipelineDetails(reportDate);
		params.put("rowcount", details.size());
		byte[] result = jasperReportService.generateReport(getReportFileName(), details, params);
		return result;
//		return jasperReportService.generateReport("RPT-SR-019-Detail.jrxml", ds, params);
	}
}
