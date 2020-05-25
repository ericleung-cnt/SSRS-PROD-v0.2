package org.mardep.ssrs.dao.dn;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;

public interface IDemandNoteRefundDao extends IBaseDao<DemandNoteRefund, Long> {
	List<DemandNoteRefund> findByDemandNoteNo(String demandNoteNo);
	List<DemandNoteRefund> findRefund(Date from, Date to, Date receiptDate);
}
