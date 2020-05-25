package org.mardep.ssrs.dao.srReport;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.srReport.ExcelChangeTonnageStore;
import org.mardep.ssrs.domain.srReport.ExcelMonthlyShipDeReg;
import org.mardep.ssrs.domain.srReport.ExcelMonthlyShipReg;
import org.mardep.ssrs.domain.srReport.ExcelNoteShipDeReg;
import org.mardep.ssrs.domain.srReport.ExcelNoteShipReg;
import org.mardep.ssrs.domain.srReport.ExcelShipRegInHKSR;

public interface IExcelShipRegDao {
	List<ExcelNoteShipReg> getNoteShipRegInMonth(Date fromDate, Date toDate);
	List<ExcelNoteShipDeReg> getNoteShipDeRegInMonth(Date fromDate, Date toDate);
	
	List<ExcelMonthlyShipReg> getMonthlyShipRegInMonth(Date fromDate, Date toDate);
	List<ExcelMonthlyShipDeReg> getMonthlyShipDeRegInMonth(Date fromDate, Date toDate);
	
	List<ExcelShipRegInHKSR> getShipRegInHKSR_TillMonth(Date toDate);
}
