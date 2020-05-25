package org.mardep.ssrs.report.generator;

import org.springframework.stereotype.Service;

@Service("RPT_SR_014")
public class RPT_SR_014 extends AbstractSrReport {

	public RPT_SR_014() {
		super("RPT-SR-014.jrxml", (rmDao, reportDate) -> { return rmDao.getRegistrationType(reportDate);});
	}
}
