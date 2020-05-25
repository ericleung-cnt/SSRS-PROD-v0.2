package org.mardep.ssrs.ebs.pojo.inbound.transcriptApp;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentItem {
	public String feeEngDesc;
	public String feeChiDesc;
	public BigDecimal revenueType;
	public BigDecimal fee;

}
