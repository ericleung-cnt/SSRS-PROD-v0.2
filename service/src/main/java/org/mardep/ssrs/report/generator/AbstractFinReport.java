package org.mardep.ssrs.report.generator;

public abstract class AbstractFinReport extends AbstractReportGenerator{

	protected final static String DATE_ON = "dateOn";
	protected final static String SORT_BY = "sortBy";
	protected final static String DATE_FROM = "dateFrom";
	protected final static String DATE_TO = "dateTo";

	@Override
	public String getReportFileName(){
		return "FIN_Template.jrxml";
	}
}
