package org.mardep.ssrs.dns.pojo.inbound.createReceipt;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.mardep.ssrs.dns.pojo.BaseRequest;
import org.mardep.ssrs.dns.pojo.DnsNamespace;

@XmlType(name="inCreateReceipt")
@XmlRootElement(name="createReceipt", namespace=DnsNamespace.DEFAULT)
public class ReceiptStatusRequest extends BaseRequest {

	private ReceiptStatus createReceipt;

	public ReceiptStatus getCreateReceipt() {
		return createReceipt;
	}

	public void setCreateReceipt(ReceiptStatus createReceipt) {
		this.createReceipt = createReceipt;
	}
	
	
}
