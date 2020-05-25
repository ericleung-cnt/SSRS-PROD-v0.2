package org.mardep.ssrs.dns.service.inbound;

import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusResponse;

public interface IReceiptService {
	
	String PENDING_REF = "PENDING_RECEIPT";

	/**
	 * 
	 * process request from DNS
	 * <ul>
	 * <li>cancel_receipt: - Follow interface mentioned in section 4.2.4 (Upload
	 * or Cancel Demand Note Payment to Other Systems) of document MD-Redev DNS
	 * – 7 Systems Interface Specification v0.15.0
	 * 
	 * <li>upload_receipt: - - Follow interface mentioned in section 4.2.4
	 * (Upload or Cancel Demand Note Payment to Other Systems) of document
	 * MD-Redev DNS – 7 Systems Interface Specification v0.15.0
	 * 
	 * @param receiptStatusRequest
	 * @return
	 * @throws SecsServiceException
	 */
	public ReceiptStatusResponse processDnsRequest(ReceiptStatusRequest receiptStatusRequest);
}
