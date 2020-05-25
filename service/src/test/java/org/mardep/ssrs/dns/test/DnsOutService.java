package org.mardep.ssrs.dns.test;

import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusResponse;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatusResponse;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusResponse;

public interface DnsOutService {

	public DemandNoteStatusResponse createDemandNote(DemandNoteStatusRequest demandNoteStatusRequest);
	public ReceiptStatusResponse updateDemandNotePayement(ReceiptStatusRequest receiptRequest);
	public RefundStatusResponse refund(RefundStatusRequest refundRequest);
}
