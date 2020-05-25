package org.mardep.ssrs.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatus;
//import org.mardep.ssrs.domain.dn.DemandNoteAging;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.domain.sr.RegMaster;

/**
 *
 * @author Leo.LIANG
 *
 */
public interface IDemandNoteService extends IBaseService{

	public DemandNoteHeader enquireDemandNoteDetail(String demandNoteNo);
	public DemandNoteHeader create(DemandNoteHeader header, boolean autopay) throws Exception;
	/**
	 *
	 * <b>PRG-MMO-024</b>
	 * <p>
	 * <b>Logic:</b></br>
	 * Mark Demand Note as cancelled
	 *
	 * @param demandNoteId
	 * @return
	 */
	public DemandNoteHeader cancel(String demandNoteNo, String cwRemark);
	public DemandNoteRefund refund(String demandNoteNo, BigDecimal refundAmount, String remarks);
	public DemandNoteRefund recommendRefund(String demandNoteNo, BigDecimal refundAmount, String remarks);
	public DemandNoteRefund confirmRecommendRefund(DemandNoteRefund entity);
	public DemandNoteItem addItem(DemandNoteItem item);
	/**
	 * set the remove reason for DEMAND_NOTE_ITEM
	 * @param id
	 * @param reason
	 */
	public void removeItem(Long id, String reason);
	/**
	 * List active records without MMO records, MMO records are those with appl_no = '0'
	 * @return
	 */
	public List<DemandNoteItem> findSrDnItems();
	/**
	 * List active records without MMO records, MMO records are those with appl_no = '0'
	 * @param end
	 * @param start
	 * @param criteria
	 * @return
	 */
	public List<DemandNoteHeader> findSrDn(Map criteria, long start, long end);
	public List<DemandNoteReceipt> findValue(String demandNote);
	public long countSrDn(Map criteria);
	public List<DemandNoteItem> findUnusedByAppl(String applNo);
	public void createAtcItem();

	/**
	 * Reserved Internal testing use only
	 * @deprecated
	 * @param resultList
	 * @param dueDate
	 */
	@Deprecated
	void createAtcDni(List<RegMaster> resultList, Date dueDate);
	String getDemandNoteNumber(String billCode, String officeCode);
	
	public DemandNoteHeader getDemandNoteHeader(String demandNoteNo);
	
	public List<DemandNoteReceipt> getDemandNoteReceipts(String demandNoteNo);
	
	public List<DemandNoteRefund> getDemandNoteRefunds(String demandNoteNo);

	public double getRefundAvailability(String demandNoteNo, String refundId);
	
	public double getPossibleRefundAmt(String refundId, DemandNoteHeader header, List<DemandNoteReceipt> receipts, List<DemandNoteRefund> refunds);
	public DemandNoteHeader create(DemandNoteHeader header, String officeCode, boolean autopay) throws Exception;
	public DemandNoteItem saveAdjustAtcAmt(DemandNoteItem entity);
	public List<DemandNoteItem> findByDemandNoteNo(String demandNoteNo);
	
	//public List<DemandNoteAging> prepareAgingRecords(List<DemandNoteHeader> dnHeaders);
	
}
