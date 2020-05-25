package org.mardep.ssrs.dns;

import javax.xml.transform.Source;

import org.mardep.ssrs.ConnectivityInfo;
import org.mardep.ssrs.dns.pojo.BaseResponse;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteRequest;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteResponse;
import org.mardep.ssrs.dns.pojo.outbound.createReceipt.ReceiptRequest;
import org.mardep.ssrs.dns.pojo.outbound.createReceipt.ReceiptResponse;
import org.mardep.ssrs.dns.pojo.outbound.refund.RefundRequest;
import org.mardep.ssrs.dns.pojo.outbound.refund.RefundResponse;

/**
 *
 * for sending SOAP message to DNS
 *
 * @author Leo.LIANG
 *
 */
public interface IDnsOutService extends ConnectivityInfo{

	/**
	 * only need to support Upload {@code Action.U} and Cancel {@code Action.C} DemandNote
	 *
	 * @param demandNoteRequest
	 * @return
	 */
	public DemandNoteResponse sendDemandNote(DemandNoteRequest demandNoteRequest);

	/**
	 *
	 * @param refundRequest
	 * @return
	 */
	public RefundResponse sendRefund(RefundRequest refundRequest);

	public BaseResponse resendFailSoap(Source source);
//	public ReceiptResponse updateDemandNotePayement(ReceiptRequest receiptRequest);

	public ReceiptResponse sendReceipt(ReceiptRequest req);

}
