package org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.dns.pojo.BaseResult;
import org.mardep.ssrs.dns.pojo.DnsNamespace;

@XmlRootElement(name="result", namespace=DnsNamespace.DEFAULT)
public class RefundStatusResult extends BaseResult {
	
	private String dnNo;
	private String refId;
	
	@XmlElement(name="dnNo")
	public String getDnNo() {
		return dnNo;
	}

	public void setDnNo(String dnNo) {
		this.dnNo = dnNo;
	}

	@XmlElement(name="refId")
	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

}
