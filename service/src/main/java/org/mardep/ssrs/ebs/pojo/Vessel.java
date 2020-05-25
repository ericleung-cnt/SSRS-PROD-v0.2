package org.mardep.ssrs.ebs.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="Ship")
public class Vessel {

	String applNo;
	String vesselName;
	String vesselChiName;
	String callSign;
	String officialNo;
	String imo;
	String regStatus;
	Date regDate;
	String vesselTypeDesc;
	BigDecimal nt;
	BigDecimal gt;

}
