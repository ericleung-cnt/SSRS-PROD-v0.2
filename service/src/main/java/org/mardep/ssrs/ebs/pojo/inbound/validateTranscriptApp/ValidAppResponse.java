package org.mardep.ssrs.ebs.pojo.inbound.validateTranscriptApp;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.EbsNamespace;

import lombok.Data;

@Data
@XmlRootElement(name="ValidAppRequest", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidAppResponse {
	
	public String applNo;
	
	public Date date;

}
