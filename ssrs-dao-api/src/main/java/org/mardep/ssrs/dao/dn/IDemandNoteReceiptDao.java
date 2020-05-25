package org.mardep.ssrs.dao.dn;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;

public interface IDemandNoteReceiptDao extends IBaseDao<DemandNoteReceipt, Long> {

	List<DemandNoteReceipt> findByDemandNoteNo(String demandNoteNo);
	List<DemandNoteReceipt> findPending();
	List<DemandNoteReceipt> findValue(String demandNoteNo);
	List<DemandNoteReceipt> findByReceiptNo(String receiptNo);

	/**
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	List<DemandNoteReceipt> findException(Date from, Date to);
	List<DemandNoteReceipt> findCancelled(Date from, Date to);
	List<Object[]> findReceiptCollected01(Date from, Date to, String sortBy);
}
