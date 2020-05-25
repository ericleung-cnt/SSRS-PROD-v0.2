package org.mardep.ssrs.report.generator;

import org.springframework.stereotype.Service;

@Service("RPT_SR_015")
public class RPT_SR_015 extends AbstractSrReport {

	public RPT_SR_015() {
		super("RPT-SR-015.jrxml", (rmDao, reportDate) -> {
			return rmDao.getBreakDownNoAndGrtOfShipsByType(reportDate);
		});

	}
}
