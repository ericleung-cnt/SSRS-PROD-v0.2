package org.mardep.ssrs.ebs.pojo.inbound.getTakeUpRateStat;

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
@XmlRootElement(name="getTakeUpRateStatResponse", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTakeUpRateStatResponse {

	public String resultType;
	@XmlElementWrapper(name="resultList")
	@XmlElement(name="result")
	public List<Result> resultList = new ArrayList<>();

	public Long transcriptTotal;
	public Long  hkRegShipTotal;



}
