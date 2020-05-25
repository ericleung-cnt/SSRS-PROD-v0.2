package org.mardep.ssrs.dao.dn;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;

public interface IDemandNoteHeaderDao extends IBaseDao<DemandNoteHeader, String> {

	public final String DEMAND_NOTE_REMINDER_1ST = "DEMAND_NOTE_REMINDER_1ST";
	public final String DEMAND_NOTE_REMINDER_2ND = "DEMAND_NOTE_REMINDER_2ND";

	Long findNextId();

	List<DemandNoteHeader> findSrDn(Map criteria, long start, long end);

	long countSrDn(Map criteria);

	/**
	 * @param id
	 *            "DEMAND_NOTE_REMINDER_1ST" or "DEMAND_NOTE_REMINDER_2ND"
	 * @return
	 */
	List<DemandNoteHeader> listForReminder(String id);

	List<DemandNoteHeader> findAge(String sortBy, Integer overDueTimeFrame, Date on);

	List<Object[]> findStatusReport(Date from, Date to, Date receiptDateFrom, Date receiptDateTo, String dnStatusParam,
			String paymentStatus, String sortBy);

	DemandNoteHeader findOld(String oldDemandNoteNo);

	List<DemandNoteHeader> findCancelled(Date from, Date to, String ebsFlag);

}
