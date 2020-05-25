package org.mardep.ssrs.dns.pojo.outbound.createReceipt;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.mardep.ssrs.dns.pojo.BaseResponse;
import org.mardep.ssrs.dns.pojo.DnsNamespace;
import org.mardep.ssrs.dns.pojo.common.CreateReceiptAction;

@XmlType(name="outCreateReceiptResponse")
@XmlRootElement(name="createReceiptResponse", namespace=DnsNamespace.DNS)
public class ReceiptResponse extends BaseResponse {

	private String receiptNo;
	private String dnNo;
	private CreateReceiptAction createReceiptAction;
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public String getDnNo() {
		return dnNo;
	}
	public void setDnNo(String dnNo) {
		this.dnNo = dnNo;
	}
	public CreateReceiptAction getCreateReceiptAction() {
		return createReceiptAction;
	}
	public void setCreateReceiptAction(CreateReceiptAction createReceiptAction) {
		this.createReceiptAction = createReceiptAction;
	}
}
