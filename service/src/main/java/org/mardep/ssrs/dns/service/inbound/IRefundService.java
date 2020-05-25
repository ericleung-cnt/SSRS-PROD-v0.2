package org.mardep.ssrs.dns.service.inbound;

import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusResponse;

public interface IRefundService{
	
	/**
	 *
	 * process request from DNS
	 * <ul>
	 * <li>Upload_refund: - Follow interface mentioned in section 4.2.3 (Upload
	 * Refund Request Status) of document MD-Redev DNS – 7 Systems Interface
	 * Specification v0.15.0
	 * 
	 * @param refundStatusRequest
	 * @return
	 */
	public RefundStatusResponse processDnsRequest(RefundStatusRequest refundStatusRequest);
}
