package org.mardep.ssrs.ebs.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class DownloadResponse extends BaseResponse {

	@XmlElement(name="docId", required=false)
	public String docId;
	@XmlElement(name = "pdf")
	public byte[] pdf = new byte[0];
}
