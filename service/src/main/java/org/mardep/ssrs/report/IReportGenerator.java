package org.mardep.ssrs.report;

import java.util.Map;

public interface IReportGenerator {

	String getReportFileName();
	byte[] generate(Map<String, Object> inputParam) throws Exception;
}
