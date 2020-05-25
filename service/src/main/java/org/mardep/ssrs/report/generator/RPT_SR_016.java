package org.mardep.ssrs.report.generator;

import org.springframework.stereotype.Service;

@Service("RPT_SR_016")
public class RPT_SR_016 extends AbstractSrReport {

	public RPT_SR_016() {
		super("RPT-SR-016.jrxml", (rmDao, reportDate) -> {
			return rmDao.getNoAndTonnage(reportDate);
		});

	}
}
