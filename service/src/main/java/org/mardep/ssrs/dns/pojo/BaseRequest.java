package org.mardep.ssrs.dns.pojo;

import javax.xml.bind.annotation.XmlTransient;

public abstract class BaseRequest {

	protected String controlId;
	@XmlTransient
	public String getControlId() {
		return controlId;
	}
	public void setControlId(String controlId) {
		this.controlId = controlId;
	}
}
