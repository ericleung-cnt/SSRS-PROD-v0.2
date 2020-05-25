package org.mardep.ssrs.dns.pojo.outbound.createDemandNote;

import java.math.BigDecimal;
import java.util.Date;

public class DemandNoteItem {
	private Integer itemNo;
	private int isRemark;
	private String particular;
	private String revenueType;
	private String feeCode;
	private BigDecimal unitPrice;
	private Integer unit;
	private BigDecimal amount=new BigDecimal(0);
	private String permitNoFrom;
	private String permitNoTo;
	private Date fromDate;
	private Date toDate;
	private String vesselName;
	private String vesselNameChi;
	private String cooNo;
	private String vesselFileNo;
	private String aipNo;
	private String operatinglicenseNo;
	private String vesselID;
	private String callSign;
	private BigDecimal vesselNetTons;
	private BigDecimal vesselGrossTons;
	private BigDecimal vesselArea;
	private Integer inboundNo;
	private Integer outboundNo;
	private String remarks;
	private String mdReferenceNumber;
	
	public Integer getItemNo() {
		return itemNo;
	}
	public void setItemNo(Integer itemNo) {
		this.itemNo = itemNo;
	}
	public int getIsRemark() {
		return isRemark;
	}
	public void setIsRemark(int isRemark) {
		this.isRemark = isRemark;
	}
	public String getParticular() {
		return particular;
	}
	public void setParticular(String particular) {
		this.particular = particular;
	}
	public String getRevenueType() {
		return revenueType;
	}
	public void setRevenueType(String revenueType) {
		this.revenueType = revenueType;
	}
	public String getFeeCode() {
		return feeCode;
	}
	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public Integer getUnit() {
		return unit;
	}
	public void setUnit(Integer unit) {
		this.unit = unit;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getPermitNoFrom() {
		return permitNoFrom;
	}
	public void setPermitNoFrom(String permitNoFrom) {
		this.permitNoFrom = permitNoFrom;
	}
	public String getPermitNoTo() {
		return permitNoTo;
	}
	public void setPermitNoTo(String permitNoTo) {
		this.permitNoTo = permitNoTo;
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
	public String getVesselID() {
		return vesselID;
	}
	public void setVesselID(String vesselID) {
		this.vesselID = vesselID;
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
	public Integer getInboundNo() {
		return inboundNo;
	}
	public void setInboundNo(Integer inboundNo) {
		this.inboundNo = inboundNo;
	}
	public Integer getOutboundNo() {
		return outboundNo;
	}
	public void setOutboundNo(Integer outboundNo) {
		this.outboundNo = outboundNo;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getMdReferenceNumber() {
		return mdReferenceNumber;
	}
	public void setMdReferenceNumber(String mdReferenceNumber) {
		this.mdReferenceNumber = mdReferenceNumber;
	}
}
