package org.mardep.ssrs.report.generator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Service;

@Service("RPT_SR_012")
public class RPT_SR_012 extends AbstractSrReport {

	public RPT_SR_012() {
		super("RPT-SR-012.jrxml", null);
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		Date reportDateFrom = (Date)inputParam.get("reportDateFrom");
		SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
		params.put("reportDateFrom", format.format(reportDateFrom));
		Date reportDateTo = (Date)inputParam.get("reportDateTo");
		params.put("reportDateTo", format.format(reportDateTo));
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		params.put("userId", currentUser);
		List<Map<String, ?>> rows = rmDao.getDiscountAtf(reportDateFrom, reportDateTo);
		params.put("totalItemsRetrieved", Integer.toString(rows.size()));
		double totalNormal = 0.0;
		double totalBilled = 0.0;
		DecimalFormat df = new DecimalFormat("$#,###.00");
		int discount = 0;
		for (Map<String, ?> row : rows) {
			totalNormal += df.parse((String) row.get("normalAmount")).doubleValue();
			totalBilled += df.parse((String) row.get("billedAmount")).doubleValue();
			if (!row.get("normalAmount").equals(row.get("billedAmount"))) {
				discount++;
			}
		}
		params.put("totalNormalAmount", df.format(totalNormal));
		params.put("totalNoOfDiscount", String.valueOf(discount));
		params.put("totalBilledAmount", df.format(totalBilled));
		return jasperReportService.generateReport(getReportFileName(), rows, params);
	}
}
