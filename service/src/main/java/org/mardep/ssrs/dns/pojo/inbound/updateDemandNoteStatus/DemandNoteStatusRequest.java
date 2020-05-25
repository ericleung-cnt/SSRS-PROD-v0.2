package org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus;

import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.dns.pojo.BaseRequest;
import org.mardep.ssrs.dns.pojo.DnsNamespace;

@XmlRootElement(name="updateDNStatus", namespace=DnsNamespace.DEFAULT)
public class DemandNoteStatusRequest extends BaseRequest {

	private DemandNoteStatus updateDNStatus;

	public DemandNoteStatus getUpdateDNStatus() {
		return updateDNStatus;
	}

	public void setUpdateDNStatus(DemandNoteStatus updateDNStatus) {
		this.updateDNStatus = updateDNStatus;
	}
	
}
