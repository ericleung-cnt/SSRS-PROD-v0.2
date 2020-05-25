package org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.dns.pojo.BaseRequest;
import org.mardep.ssrs.dns.pojo.DnsNamespace;
import org.mardep.ssrs.dns.pojo.common.RefundAction;

public class UpdateRefundStatus extends BaseRequest {
	
	private RefundAction Action;
	
	private RefundStatus refund;

	@XmlElement(name="refund")
	public RefundStatus getRefund() {
		return refund;
	}

	public void setRefund(RefundStatus refund) {
		this.refund = refund;
	}

	@XmlElement(name="Action")
	public RefundAction getAction() {
		return Action;
	}

	public void setAction(RefundAction action) {
		Action = action;
	}


}
