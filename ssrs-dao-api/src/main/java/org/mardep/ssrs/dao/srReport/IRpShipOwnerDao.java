package org.mardep.ssrs.dao.srReport;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.srReport.RpShipOwner;

public interface IRpShipOwnerDao {
	public List<Representative> getUniqueRpList();

	List<RpShipOwner> getForDate(Date forDate);
}
