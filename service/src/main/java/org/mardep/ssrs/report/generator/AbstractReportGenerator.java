package org.mardep.ssrs.report.generator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.service.AbstractService;
import org.mardep.ssrs.service.IJasperReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractReportGenerator extends AbstractService implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public final static DateFormat REPORT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	IJasperReportService jasperReportService;

	protected final static String REPORT_ID="reportId";
	protected final static String COMPANY_NAME="companyName";
	protected final static String REPORT_TITLE="reportTitle";
	protected final static String REPORT_SUB_TITLE="reportSubTitle";
	protected final static String REPORT_DATE="reportDate";
	protected final static String PART="part";
	protected final static String USER_ID="userId";

	public abstract String getReportFileName();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public byte[] generate(List listOfEntity, Map<String, Object> map) throws Exception {
		return jasperReportService.generateReport(getReportFileName(), listOfEntity, map);
	}
	
	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
