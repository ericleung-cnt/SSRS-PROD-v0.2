package org.mardep.ssrs.dns.pojo.outbound;

import javax.xml.bind.annotation.XmlElement;

public class RequestFile {

	private byte[] dFile;
	
	@XmlElement(name="dfile")
	public byte[] getdFile() {
		return dFile;
	}
	public void setdFile(byte[] dFile) {
		this.dFile = dFile;
	}
}
