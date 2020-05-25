package org.mardep.ssrs.service;

import java.math.BigDecimal;
import java.util.Date;

import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.Action;
import org.mardep.ssrs.domain.dns.ControlAction;
import org.mardep.ssrs.domain.dns.ControlData;
import org.mardep.ssrs.domain.dns.ControlEntity;

/**
 *
 * @author Leo.LIANG
 *
 */
public interface IDnsService extends IBaseService{

	@Deprecated
	ControlData create(String demandNoteNo, ControlEntity controlEntity, ControlAction controlAction);
	/**
	 * sending DemandNote request to DNS
	 * @param controlData
	 * @param demandNoteNo
	 * @param action
	 * @param autopay
	 * @return
	 */
	public void createDemandNote(String demandNoteNo, Action action, boolean autopay);
	/**
	 * Sending Refund Request to DNS
	 *
	 * @param controlData
	 * @param demandNoteId
	 */
	public void refundDemandNote(Long refundId);
	void updatePayment(String receiptNo, String dnNo, BigDecimal amount, Date receiptDate, String paymentType);
	void cancelPayment(String receiptNo, String demandNoteNo, BigDecimal amount, Date receiptDate,
			String paymentType);

//	SoapMessageOut insertOutMsg(SoapMessageOut soapOut);

//	ControlData readOne();
//	void resendFailSoapOut(Long soapOutId);

//	void resendFailControlData(Long controlDataId);
//	void migrateDNtoDNS(String demandNoteId, Long cid, byte[] file);
//	void migrateCancelDNtoDNS(ControlData controlData, String dnId);
//	void migrateReceipttoDNS(ControlData controlData, String receiptId, String action);
//	List<ControlData> readForScheduler();
//	Long findCountBy(ControlEntity ce, ControlAction ca, String entityId);
//	void setSoapSchedulerEnable(Boolean soapHandlerEnable);
//	Boolean getSoapSchedulerEnable();
//
//
//	void updateOutMsg(SoapMessageOut o);
//	SoapMessageOut findOutByControlId(Long controlId);
//	SoapMessageIn insertInMsg(SoapMessageIn soapIn);
//	void updateInMsg(SoapMessageIn o);
//	String findDetailByIds(List<Long> idList);
//	String checkCanResend(List<Long> idList);
}
