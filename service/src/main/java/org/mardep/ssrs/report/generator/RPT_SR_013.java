package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Service;

@Service("RPT_SR_013")
public class RPT_SR_013 extends AbstractSrReport {

	public RPT_SR_013() {
		super("RPT-SR-013.jrxml", (rmDao, reportDate) -> {
			return rmDao.getShipsByShipTypes(reportDate);
		});
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		logger.info("Report Date:{}", reportDate);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reportDate", createDateFormat().format(reportDate));
		params.put("reportDateTime", new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(reportDate));
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		params.put("userId", currentUser);
		List<Map<String, Object>> rows = rmDao.getShipsByShipTypes(reportDate);
		return jasperReportService.generateReport(getReportFileName(), rows, params);
	}
}
