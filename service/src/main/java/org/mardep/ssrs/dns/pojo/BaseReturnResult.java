package org.mardep.ssrs.dns.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class BaseReturnResult {

	private ResultCode resultCode;
	
	private String result;
	private String description;
	

	public BaseReturnResult (){
		
	}
	
	public BaseReturnResult (ResultCode resultCode){
		this.resultCode = resultCode;
		this.result = resultCode.getCode();
		this.description = resultCode.getDescription();
	}
	
	@XmlElement
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	@XmlElement
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlTransient
	public ResultCode getResultCode() {
		return resultCode;
	}
	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
		this.result = resultCode.getCode();
		this.description = resultCode.getDescription();
	}
}
