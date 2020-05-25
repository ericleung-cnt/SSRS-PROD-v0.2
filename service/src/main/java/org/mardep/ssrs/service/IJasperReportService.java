package org.mardep.ssrs.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;

public interface IJasperReportService {

	public <T> byte[] generateReport(String jrxmlFileName, List<T> secsEntity, Map<String, Object> map) throws JRException;
	public JasperReport getJasperReport(String filePath);

	public byte[] generateReport(String jrxmlFileName, Map<String, Object> jrxmlParams, JRDataSource dataSource) throws JRException;
	public byte[] generateReport(String jrxmlFileName, Map<String, Object> jrxmlParams, JRDataSource dataSource, String destFilename, boolean needConnection) throws JRException;
}
