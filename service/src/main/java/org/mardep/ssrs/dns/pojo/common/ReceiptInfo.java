package org.mardep.ssrs.dns.pojo.common;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class ReceiptInfo {

	private String receiptNo;
	private Date receiptDate;
	private String dnNo;
	private String billCode;
	private String machineID;
	private BigDecimal receiptAmount;
	private String paymentRef;
	private List<ReceiptItem> paymentList;
	private List<ChargeItem> chargeList;
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public Date getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}
	public String getDnNo() {
		return dnNo;
	}
	public void setDnNo(String dnNo) {
		this.dnNo = dnNo;
	}
	public String getBillCode() {
		return billCode;
	}
	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}
	public String getMachineID() {
		return machineID;
	}
	public void setMachineID(String machineID) {
		this.machineID = machineID;
	}
	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}
	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
	
	@XmlElementWrapper(name="paymentList")
	@XmlElement(name="payment")
	public List<ReceiptItem> getPaymentList() {
		return paymentList;
	}
	public void setPaymentList(List<ReceiptItem> paymentList) {
		this.paymentList = paymentList;
	}
	@XmlElementWrapper(name="chargeItemList")
	@XmlElement(name="chargeItem")
	public List<ChargeItem> getChargeList() {
		return chargeList;
	}
	public String getPaymentRef() {
		return paymentRef;
	}
	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}
	public void setChargeList(List<ChargeItem> chargeList) {
		this.chargeList = chargeList;
	}
}
