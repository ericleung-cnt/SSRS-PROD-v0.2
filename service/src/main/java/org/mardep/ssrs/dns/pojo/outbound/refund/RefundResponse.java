package org.mardep.ssrs.dns.pojo.outbound.refund;

import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.dns.pojo.BaseResponse;
import org.mardep.ssrs.dns.pojo.DnsNamespace;

@XmlRootElement(name="refundRequestResponse", namespace=DnsNamespace.DNS)
public class RefundResponse extends BaseResponse{

}
