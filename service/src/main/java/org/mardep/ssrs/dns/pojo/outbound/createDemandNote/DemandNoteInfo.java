package org.mardep.ssrs.dns.pojo.outbound.createDemandNote;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.mardep.ssrs.domain.constant.Cons;

public class DemandNoteInfo {
	private String dnNo;
	private Date lastUpdateDatetime;
	private Date issueDate;
	private Date dueDate;
	private Date valueDate;
	private BigDecimal amountTTL;
	private String billCode = Cons.DNS_BILL_CODE;
	private String deptRef;
	private String enqTel1;
	private String enqTel2;
	private String enqTel3;
	private Integer dnStatus;
	private Date fromDate;
	private Date toDate;
	private String pmNo;
	private BigDecimal pmLength;
	private String vesselName;
	private String vesselNameChi;
	private String cooNo;
	private String vesselFileNo;
	private String aipNo;
	private String operatinglicenseNo;
	private String callSign;
	private BigDecimal vesselNetTons;
	private BigDecimal vesselGrossTons;
	private BigDecimal vesselArea;
	private String userCode;
	private String officeCode;
	private String sectionCode;
	private String payerNameChi;
	private String payerName;
	private String payerAddr1;
	private String payerAddr2;
	private String payerAddr3;
	private String payerAddr4;
	private String agentNameChi;
	private String agentName;
	private String agentAddr1;
	private String agentAddr2;
	private String agentAddr3;
	private String agentAddr4;
	private String remarks;
	private int autopayRequest;
	private int isAutopay;
	private String payerAccNo;
	private String debtorRef;
	private String debtorName;
	private Integer autopayLimit;

	private List<DemandNoteItem> itemList;

	public String getDnNo() {
		return dnNo;
	}
	public void setDnNo(String dnNo) {
		this.dnNo = dnNo;
	}
	public Date getLastUpdateDatetime() {
		return lastUpdateDatetime;
	}
	public void setLastUpdateDatetime(Date lastUpdateDatetime) {
		this.lastUpdateDatetime = lastUpdateDatetime;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public Date getValueDate() {
		return valueDate;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}
	public BigDecimal getAmountTTL() {
		return amountTTL;
	}
	public void setAmountTTL(BigDecimal amountTTL) {
		this.amountTTL = amountTTL;
	}
	public String getBillCode() {
		return billCode;
	}
	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}
	public String getDeptRef() {
		return deptRef;
	}
	public void setDeptRef(String deptRef) {
		this.deptRef = deptRef;
	}
	public String getEnqTel1() {
		return enqTel1;
	}
	public void setEnqTel1(String enqTel1) {
		this.enqTel1 = enqTel1;
	}
	public String getEnqTel2() {
		return enqTel2;
	}
	public void setEnqTel2(String enqTel2) {
		this.enqTel2 = enqTel2;
	}
	public String getEnqTel3() {
		return enqTel3;
	}
	public void setEnqTel3(String enqTel3) {
		this.enqTel3 = enqTel3;
	}
	public Integer getDnStatus() {
		return dnStatus;
	}
	public void setDnStatus(Integer dnStatus) {
		this.dnStatus = dnStatus;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public String getPmNo() {
		return pmNo;
	}
	public void setPmNo(String pmNo) {
		this.pmNo = pmNo;
	}
	public BigDecimal getPmLength() {
		return pmLength;
	}
	public void setPmLength(BigDecimal pmLength) {
		this.pmLength = pmLength;
	}
	public String getVesselName() {
		return vesselName;
	}
	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}
	public String getVesselNameChi() {
		return vesselNameChi;
	}
	public void setVesselNameChi(String vesselNameChi) {
		this.vesselNameChi = vesselNameChi;
	}
	public String getCooNo() {
		return cooNo;
	}
	public void setCooNo(String cooNo) {
		this.cooNo = cooNo;
	}
	public String getVesselFileNo() {
		return vesselFileNo;
	}
	public void setVesselFileNo(String vesselFileNo) {
		this.vesselFileNo = vesselFileNo;
	}
	public String getAipNo() {
		return aipNo;
	}
	public void setAipNo(String aipNo) {
		this.aipNo = aipNo;
	}
	public String getOperatinglicenseNo() {
		return operatinglicenseNo;
	}
	public void setOperatinglicenseNo(String operatinglicenseNo) {
		this.operatinglicenseNo = operatinglicenseNo;
	}
	public String getCallSign() {
		return callSign;
	}
	public void setCallSign(String callSign) {
		this.callSign = callSign;
	}
	public BigDecimal getVesselNetTons() {
		return vesselNetTons;
	}
	public void setVesselNetTons(BigDecimal vesselNetTons) {
		this.vesselNetTons = vesselNetTons;
	}

	public BigDecimal getVesselGrossTons() {
		return vesselGrossTons;
	}
	public void setVesselGrossTons(BigDecimal vesselGrossTons) {
		this.vesselGrossTons = vesselGrossTons;
	}
	public BigDecimal getVesselArea() {
		return vesselArea;
	}
	public void setVesselArea(BigDecimal vesselArea) {
		this.vesselArea = vesselArea;
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
	public String getSectionCode() {
		return sectionCode;
	}
	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}
	public String getPayerNameChi() {
		return payerNameChi;
	}
	public void setPayerNameChi(String payerNameChi) {
		this.payerNameChi = payerNameChi;
	}
	public String getPayerName() {
		return payerName;
	}
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}
	public String getPayerAddr1() {
		return payerAddr1;
	}
	public void setPayerAddr1(String payerAddr1) {
		this.payerAddr1 = payerAddr1;
	}
	public String getPayerAddr2() {
		return payerAddr2;
	}
	public void setPayerAddr2(String payerAddr2) {
		this.payerAddr2 = payerAddr2;
	}
	public String getPayerAddr3() {
		return payerAddr3;
	}
	public void setPayerAddr3(String payerAddr3) {
		this.payerAddr3 = payerAddr3;
	}
	public String getPayerAddr4() {
		return payerAddr4;
	}
	public void setPayerAddr4(String payerAddr4) {
		this.payerAddr4 = payerAddr4;
	}
	public String getAgentNameChi() {
		return agentNameChi;
	}
	public void setAgentNameChi(String agentNameChi) {
		this.agentNameChi = agentNameChi;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentAddr1() {
		return agentAddr1;
	}
	public void setAgentAddr1(String agentAddr1) {
		this.agentAddr1 = agentAddr1;
	}
	public String getAgentAddr2() {
		return agentAddr2;
	}
	public void setAgentAddr2(String agentAddr2) {
		this.agentAddr2 = agentAddr2;
	}
	public String getAgentAddr3() {
		return agentAddr3;
	}
	public void setAgentAddr3(String agentAddr3) {
		this.agentAddr3 = agentAddr3;
	}
	public String getAgentAddr4() {
		return agentAddr4;
	}
	public void setAgentAddr4(String agentAddr4) {
		this.agentAddr4 = agentAddr4;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getAutopayRequest() {
		return autopayRequest;
	}
	public void setAutopayRequest(int autopayRequest) {
		this.autopayRequest = autopayRequest;
	}
	public int getIsAutopay() {
		return isAutopay;
	}
	public void setIsAutopay(int isAutopay) {
		this.isAutopay = isAutopay;
	}
	public String getPayerAccNo() {
		return payerAccNo;
	}
	public void setPayerAccNo(String payerAccNo) {
		this.payerAccNo = payerAccNo;
	}
	public String getDebtorRef() {
		return debtorRef;
	}
	public void setDebtorRef(String debtorRef) {
		this.debtorRef = debtorRef;
	}
	public String getDebtorName() {
		return debtorName;
	}
	public void setDebtorName(String debtorName) {
		this.debtorName = debtorName;
	}
	public Integer getAutopayLimit() {
		return autopayLimit;
	}
	public void setAutopayLimit(Integer autopayLimit) {
		this.autopayLimit = autopayLimit;
	}
	@XmlElementWrapper(name="demandNoteItemList")
	@XmlElement(name="demandNoteItem")
	public List<DemandNoteItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<DemandNoteItem> itemList) {
		this.itemList = itemList;
	}
}
