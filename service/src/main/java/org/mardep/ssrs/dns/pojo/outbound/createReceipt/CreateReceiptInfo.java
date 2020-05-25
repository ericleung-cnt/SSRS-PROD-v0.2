package org.mardep.ssrs.dns.pojo.outbound.createReceipt;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.mardep.ssrs.dns.pojo.common.ReceiptItem;

public class CreateReceiptInfo {

	private String receiptNo;
	private Date receiptDate;
	private String dnNo;
	private String billCode;
	private String machineID;
	private BigDecimal receiptAmount;
	private List<ReceiptItem> paymentList;
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
}
