package org.mardep.ssrs.dao.ocr;

import org.mardep.ssrs.domain.sr.Mortgage;

public interface IOcrMortgageDao {
	public Mortgage getLatestMortgageByApplNo(String applNo);
}
