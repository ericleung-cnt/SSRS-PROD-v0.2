package org.mardep.ssrs.report.generator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

@Service("RPT_SR_018")
public class RPT_SR_018 extends AbstractSrReport {

	public RPT_SR_018() {
		super("RPT-SR-018.jrxml", null);
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date) inputParam.get("reportDate");
		inputParam.put("reportMonth", new SimpleDateFormat("yyyy-MM").format(reportDate));
		List<List<Map<String, ?>>> ownershipReports = rmDao.getOwnershipReport(reportDate);
		inputParam.put("userId", UserContextThreadLocalHolder.getCurrentUserId());
		DecimalFormat format = new DecimalFormat("00");
		HashMap row = new HashMap();
		for (int i = 0; i < ownershipReports.size(); i++) {
			List<Map<String, ?>> subrpt = (List<Map<String, ?>>) ownershipReports.get(i);
			JasperReport jasperReport = jasperReportService.getJasperReport("RPT-SR-018-subreport"
					+ format.format(i+1)
					+ ".jrxml");
			inputParam.put("SUBREPORT_" + (i+1), jasperReport);
			row.put("SUBREPORTDS_" + (i+1), ownershipReports.get(i));
		}

		return jasperReportService.generateReport("RPT-SR-018.jrxml", Arrays.asList(row), inputParam);
	}
}
