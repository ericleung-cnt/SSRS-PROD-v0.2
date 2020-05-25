package org.mardep.ssrs.dao.dn;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.dn.DemandNoteItem;

public interface IDemandNoteItemDao extends IBaseDao<DemandNoteItem, Long> {
	List<DemandNoteItem> findByDemandNoteNo(String demandNoteNo);

	List<DemandNoteItem> findSrDnItems();

	List<DemandNoteItem> findUnusedByApplNo(String applNo);

	/**
	 * @param imoNoList if null, the list of Not Handled ATC will be retrieved
	 * @return
	 */
	List<Object[]> getOutstandingDn(List<String> imoNoList);
	
	DemandNoteItem getLastAtc(String applNo);

	List<Map<String, Object>> outstandingReport(Date start, Date end);
}
