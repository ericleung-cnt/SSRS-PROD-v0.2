package org.mardep.ssrs.dns.pojo.inbound.createReceipt;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.dns.pojo.BaseResult;
import org.mardep.ssrs.dns.pojo.DnsNamespace;

@XmlRootElement(name="result", namespace=DnsNamespace.DEFAULT)
public class ReceiptStatusResult extends BaseResult {
	
	private String receiptNo;
	private String dnNo;

	@XmlElement
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	@XmlElement
	public String getDnNo() {
		return dnNo;
	}
	public void setDnNo(String dnNo) {
		this.dnNo = dnNo;
	}

}
