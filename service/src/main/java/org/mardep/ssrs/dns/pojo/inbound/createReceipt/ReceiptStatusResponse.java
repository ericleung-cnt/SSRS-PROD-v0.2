package org.mardep.ssrs.dns.pojo.inbound.createReceipt;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.mardep.ssrs.dns.pojo.BaseResponse;
import org.mardep.ssrs.dns.pojo.DnsNamespace;

@XmlType(name="inCreateReceiptResponse")
@XmlRootElement(name="createReceiptResponse", namespace=DnsNamespace.DEFAULT)
public class ReceiptStatusResponse extends BaseResponse {
	
}
