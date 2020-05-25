package org.mardep.ssrs.report.generator;

import org.springframework.stereotype.Service;

@Service("RPT_SR_023")
public class RPT_SR_023 extends AbstractSrReport {

	public RPT_SR_023() {
		super("RPT-SR-023.jrxml", (rmDao, reportDate) -> {
			//return rmDao.getTonnageDistribution(reportDate);
			return rmDao.getTonnageDistributionNewScale(reportDate);
		});

	}
}
