package org.mardep.ssrs.ebs.pojo.inbound.transcriptApp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.EbsNamespace;
import org.mardep.ssrs.ebs.pojo.Result;

import lombok.Data;

@Data
@XmlRootElement(name="submitTranscriptResponse", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class SubmitTranscriptResponse {

	public String status;
	public String dnNo;
	public String txnCode;
	public BigDecimal totalAmount;
	public String docId;
	@XmlElement(required=false)
	public String resultType;

	@XmlElementWrapper(name = "resultList")
	@XmlElement(name = "result")
	public List<Result> resultList = new ArrayList<Result>();

}
