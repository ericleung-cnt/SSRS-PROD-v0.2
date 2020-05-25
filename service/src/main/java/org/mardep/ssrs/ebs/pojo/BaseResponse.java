package org.mardep.ssrs.ebs.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseResponse {

	public static final String RESULT_TYPE_SYSTEM_ERROR = "E";

	public static final String RESULT_TYPE_BUSINESS_ERROR = "M";

	@XmlElement(name="resultType", required=false)
	public String resultType;

	@XmlElementWrapper(name = "resultList")
	@XmlElement(name = "result")
	public List<Result> resultList = new ArrayList<Result>();

}
