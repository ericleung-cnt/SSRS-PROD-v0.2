package org.mardep.ssrs.dns.pojo.outbound.createDemandNote;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.dns.pojo.BaseRequest;
import org.mardep.ssrs.dns.pojo.DnsNamespace;
import org.mardep.ssrs.dns.pojo.outbound.RequestFile;

@XmlRootElement(name="createDN", namespace=DnsNamespace.DNS)
public class DemandNoteRequest extends BaseRequest {

//	private String controlId;
	private Action action;
	private DemandNoteInfo demandNoteInfo;
	private RequestFile file;
//	private byte[] file;
//	@XmlTransient
//	public String getControlId() {
//		return controlId;
//	}
//	public void setControlId(String controlId) {
//		this.controlId = controlId;
//	}
	@XmlElement
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	@XmlElement(name="demandNote")
	public DemandNoteInfo getDemandNoteInfo() {
		return demandNoteInfo;
	}
	public void setDemandNoteInfo(DemandNoteInfo demandNoteInfo) {
		this.demandNoteInfo = demandNoteInfo;
	}
	@XmlElement(name="file")
	public RequestFile getFile() {
		return file;
	}
	public void setFile(RequestFile file) {
		this.file = file;
	}
}
