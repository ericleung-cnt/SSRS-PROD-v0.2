package org.mardep.ssrs.dns.pojo.outbound.createDemandNote;

import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.dns.pojo.BaseResponse;
import org.mardep.ssrs.dns.pojo.DnsNamespace;

@XmlRootElement(name="createDNResponse", namespace=DnsNamespace.DNS)
public class DemandNoteResponse extends BaseResponse{

}
