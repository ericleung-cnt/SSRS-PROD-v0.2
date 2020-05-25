package org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus;

import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.dns.pojo.BaseRequest;
import org.mardep.ssrs.dns.pojo.DnsNamespace;

@XmlRootElement(name="UpdateReFundStatus", namespace=DnsNamespace.DEFAULT)
public class RefundStatusRequest extends BaseRequest {
	
	private UpdateRefundStatus updateRefundStatus;

	public UpdateRefundStatus getUpdateRefundStatus() {
		return updateRefundStatus;
	}

	public void setUpdateRefundStatus(UpdateRefundStatus updateRefundStatus) {
		this.updateRefundStatus = updateRefundStatus;
	}


}
