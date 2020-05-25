package org.mardep.ssrs.ebs.pojo.inbound.isSettled;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.EbsNamespace;
import org.mardep.ssrs.ebs.pojo.Result;

@XmlRootElement(name="isSettledResponse", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class IsSettledResponse {

	@XmlElement(name="resultType", required=false)
	public String resultType;

	@XmlElementWrapper(name = "resultList")
	@XmlElement(name = "result")
	public List<Result> resultList = new ArrayList<Result>();

	@XmlElementWrapper(name = "dnList")
	@XmlElement(name = "dn")
	public List<IsSettledDn> dnList = new ArrayList<IsSettledDn>();
}
