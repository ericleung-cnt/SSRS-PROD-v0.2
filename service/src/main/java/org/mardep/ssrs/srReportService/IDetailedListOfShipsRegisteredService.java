package org.mardep.ssrs.srReportService;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.domain.srReport.DetailedListOfShipsRegistered;
import org.mardep.ssrs.domain.srReport.RegisteredShip;

public interface IDetailedListOfShipsRegisteredService {
	public List<RegisteredShip> getDetailedListOfShipsRegistered(Date toDate);
}
