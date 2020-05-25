package org.mardep.ssrs.srReportService;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.domain.srReport.RpShipOwner;

public interface IListOfRpService {
	public List<RpShipOwner> getRpShipOwner(Date forDate);
}
