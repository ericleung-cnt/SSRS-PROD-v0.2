package org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.dns.pojo.BaseResult;
import org.mardep.ssrs.dns.pojo.DnsNamespace;

@XmlRootElement(name="result", namespace=DnsNamespace.DEFAULT)
public class DemandNoteStatusResult extends BaseResult{
	
	private String dnNo;
	
	@XmlElement(name="dnNo")
	public String getDnNo() {
		return dnNo;
	}

	public void setDnNo(String dnNo) {
		this.dnNo = dnNo;
	}
	
}
