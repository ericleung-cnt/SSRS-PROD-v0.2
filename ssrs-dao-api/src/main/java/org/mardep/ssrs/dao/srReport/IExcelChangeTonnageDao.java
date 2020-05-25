package org.mardep.ssrs.dao.srReport;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.domain.srReport.ExcelChangeTonnageStore;

public interface IExcelChangeTonnageDao {
	List<ExcelChangeTonnageStore> get();

	int removeOldData();

	int insertCurrentMonthData(Date fromDate, Date toDate);

	int updateFirstTxnOfCurrentMonthData(Date fromDate, Date toDate);

	int updateLastTxnOfLastMonthData(Date fromDate, Date toDate);
}
