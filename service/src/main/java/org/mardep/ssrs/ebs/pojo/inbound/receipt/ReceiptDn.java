package org.mardep.ssrs.ebs.pojo.inbound.receipt;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ReceiptDn {

	public String dnNo;
	public Calendar receiptDate;
	public String receiptNo = "";
	public BigDecimal amount;

}
