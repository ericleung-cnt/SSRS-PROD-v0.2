package org.mardep.ssrs.ebs.pojo.inbound.transcriptApp;

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
@XmlRootElement(name="validateTranscriptResponse", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidateTranscriptResponse {
	public static final String STATUS_APPROVED = "A";
	public static final String STATUS_REJECTED = "R";

	public String status;
	public String resultType;

	@XmlElementWrapper(name = "resultList")
	@XmlElement(name = "result")
	public List<Result> resultList = new ArrayList<Result>();

	@XmlElementWrapper(name = "paymentItemList")
	@XmlElement(name = "paymentItem")
	public List<PaymentItem> paymentItem = new ArrayList<>();
}
