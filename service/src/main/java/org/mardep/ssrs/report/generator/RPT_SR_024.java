package org.mardep.ssrs.report.generator;

import org.springframework.stereotype.Service;

@Service("RPT_SR_024")
public class RPT_SR_024 extends AbstractSrReport {

	public RPT_SR_024() {
		super("RPT-SR-024.jrxml", (rmDao, reportDate) -> {
			return rmDao.getCompanyRanking(reportDate);
		});

	}
}
