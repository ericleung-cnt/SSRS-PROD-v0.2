package org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

public class RefundStatus {
	private String dnNo;
	private String userCode;
	private String officeCode;
	private String Remark;
	private String billCode;
	private int dnStatus;
	private String refundVouchorNo;
	private Date rePayDate;
	private Date reVouDate;
	private BigDecimal reFundAmount;
	private String refId;
	
	public String getDnNo() {
		return dnNo;
	}
	public void setDnNo(String dnNo) {
		this.dnNo = dnNo;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getOfficeCode() {
		return officeCode;
	}
	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}
	
	@XmlElement(name="Remark")
	public String getRemark() {
		return Remark;
	}
	public void setRemark(String remark) {
		Remark = remark;
	}
	public String getBillCode() {
		return billCode;
	}
	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}
	public int getDnStatus() {
		return dnStatus;
	}
	public void setDnStatus(int dnStatus) {
		this.dnStatus = dnStatus;
	}
	public String getRefundVouchorNo() {
		return refundVouchorNo;
	}
	public void setRefundVouchorNo(String refundVouchorNo) {
		this.refundVouchorNo = refundVouchorNo;
	}
	public Date getRePayDate() {
		return rePayDate;
	}
	public void setRePayDate(Date rePayDate) {
		this.rePayDate = rePayDate;
	}
	public Date getReVouDate() {
		return reVouDate;
	}
	public void setReVouDate(Date reVouDate) {
		this.reVouDate = reVouDate;
	}
	public BigDecimal getReFundAmount() {
		return reFundAmount;
	}
	public void setReFundAmount(BigDecimal reFundAmount) {
		this.reFundAmount = reFundAmount;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
}
