package org.mardep.ssrs.dns.pojo.outbound.createReceipt;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.mardep.ssrs.dns.pojo.BaseRequest;
import org.mardep.ssrs.dns.pojo.DnsNamespace;
import org.mardep.ssrs.dns.pojo.common.CreateReceiptAction;

@XmlType(name="outCreateReceipt")
@XmlRootElement(name="createReceipt", namespace=DnsNamespace.DNS)
public class ReceiptRequest extends BaseRequest {

//	private String controlId;
	private CreateReceiptAction createReceiptAction;
	private CreateReceiptInfo receiptInfo;
//	@XmlTransient
//	public String getControlId() {
//		return controlId;
//	}
//	public void setControlId(String controlId) {
//		this.controlId = controlId;
//	}
	@XmlElement(name="action")
	public CreateReceiptAction getCreateReceiptAction() {
		return createReceiptAction;
	}
	public void setCreateReceiptAction(CreateReceiptAction createReceiptAction) {
		this.createReceiptAction = createReceiptAction;
	}
	
	@XmlElement(name="receipt")
	public CreateReceiptInfo getReceiptInfo() {
		return receiptInfo;
	}
	public void setReceiptInfo(CreateReceiptInfo receiptInfo) {
		this.receiptInfo = receiptInfo;
	}
}
