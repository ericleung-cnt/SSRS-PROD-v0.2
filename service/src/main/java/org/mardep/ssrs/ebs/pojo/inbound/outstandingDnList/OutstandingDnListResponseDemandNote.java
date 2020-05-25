package org.mardep.ssrs.ebs.pojo.inbound.outstandingDnList;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class OutstandingDnListResponseDemandNote {

	public String applNo;
	public String shipName;
	public String shipCName;
	public Calendar genDate;
	public Calendar atcDueDate;
	public BigDecimal amount;
	public Long itemNo;
	public BigDecimal regNetTon;
	public String imoNo; 

}
