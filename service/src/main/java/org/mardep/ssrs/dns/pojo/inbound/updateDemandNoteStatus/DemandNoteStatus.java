package org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

public class DemandNoteStatus {
	private String dnNo;
	private String userCode;
	private String officeCode;
	private String Remark;
	private String billCode;
	private Date writeOffDate;
	private int dnStatus;
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
		this.Remark = remark;
	}
	public String getBillCode() {
		return billCode;
	}
	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}
	public Date getWriteOffDate() {
		return writeOffDate;
	}
	public void setWriteOffDate(Date writeOffDate) {
		this.writeOffDate = writeOffDate;
	}
	public int getDnStatus() {
		return dnStatus;
	}
	public void setDnStatus(int dnStatus) {
		this.dnStatus = dnStatus;
	}
}
