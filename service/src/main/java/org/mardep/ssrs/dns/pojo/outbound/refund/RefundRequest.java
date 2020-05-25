package org.mardep.ssrs.dns.pojo.outbound.refund;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.dns.pojo.BaseRequest;
import org.mardep.ssrs.dns.pojo.DnsNamespace;

@XmlRootElement(name="refundRequest", namespace=DnsNamespace.DNS)
public class RefundRequest extends BaseRequest{

	private RefundInfo refundInfo;

	@XmlElement(name="refundInfo")
	public RefundInfo getRefundInfo() {
		return refundInfo;
	}
	public void setRefundInfo(RefundInfo refundInfo) {
		this.refundInfo = refundInfo;
	}
}
