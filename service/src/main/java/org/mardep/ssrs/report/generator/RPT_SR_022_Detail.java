package org.mardep.ssrs.report.generator;

import org.springframework.stereotype.Service;

@Service("RPT_SR_022_Detail")
public class RPT_SR_022_Detail extends AbstractSrReport {

	public RPT_SR_022_Detail() {
		super("RPT-SR-022-Detail.jrxml", (rmDao, reportDate)->{
			return rmDao.getShipRegAnnualReportDetail(reportDate);
		});
	}

}
