package org.mardep.ssrs.report.generator;

import org.springframework.stereotype.Service;

@Service("RPT_SR_017")
public class RPT_SR_017 extends AbstractSrReport {

	public RPT_SR_017() {
		super("RPT-SR-017.jrxml", (rmDao, reportDate) -> {
			return rmDao.getOwnerCatergory(reportDate);
		});

	}
}
