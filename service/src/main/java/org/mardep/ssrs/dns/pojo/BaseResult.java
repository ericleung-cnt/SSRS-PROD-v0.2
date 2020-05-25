package org.mardep.ssrs.dns.pojo;

import javax.xml.bind.annotation.XmlElement;

public class BaseResult {

	private BaseReturnResult baseReturnResult;

	private String dnNo;
	private String refId;
	private String receiptNo;
	
	@XmlElement(name="returnResult")
	public BaseReturnResult getBaseReturnResult() {
		return baseReturnResult;
	}

	public void setBaseReturnResult(BaseReturnResult baseReturnResult) {
		this.baseReturnResult = baseReturnResult;
	}
	@XmlElement
	public String getDnNo() {
		return dnNo;
	}

	public void setDnNo(String dnNo) {
		this.dnNo = dnNo;
	}
	@XmlElement
	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}
	@XmlElement
	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

}
