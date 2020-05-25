package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.service.IJasperReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractSrReport implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IRegMasterDao rmDao;

	@Autowired
	IJasperReportService jasperReportService;

	private final String reportName;

	private final BiFunction<IRegMasterDao, Date, List<Map<String, Object>>> function;

	public AbstractSrReport(String reportName, BiFunction<IRegMasterDao, Date, List<Map<String, Object>>> function) {
		this.reportName = reportName;
		this.function = function;
	}

	@Override
	public String getReportFileName() {
		return reportName;
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		logger.info("Report Date:{}", reportDate);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reportDate", createDateFormat().format(reportDate));
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		params.put("userId", currentUser);
		List<Map<String, Object>> rows = function.apply(rmDao, reportDate);
		return jasperReportService.generateReport(getReportFileName(), rows, params);
	}

	public SimpleDateFormat createDateFormat() {
		return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	}

}
