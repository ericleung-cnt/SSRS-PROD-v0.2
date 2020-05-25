package org.mardep.ssrs.dns.pojo;

import javax.xml.bind.annotation.XmlElement;

public abstract class BaseResponse {

	protected BaseResult baseResult;

	@XmlElement(name="result")
	public BaseResult getBaseResult() {
		 return baseResult;
	}

	public void setBaseResult(BaseResult baseResult) {
		this.baseResult = baseResult;
	}

}
