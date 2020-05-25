package org.mardep.ssrs.ebs.pojo.inbound.rejectAutopay;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DemandNote {

	public String dnNo = "";
	public byte[] pdf = new byte[0];
	public String receiptNo;
	public Calendar receiptDate;
	public BigDecimal receiptAmt;


}
