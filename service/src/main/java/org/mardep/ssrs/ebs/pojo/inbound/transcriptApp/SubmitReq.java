package org.mardep.ssrs.ebs.pojo.inbound.transcriptApp;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.EbsNamespace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="submitTranscriptRequest", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class SubmitReq {
	public static final int PAYMENT_METHOD_AUTOPAY = 1;

	public String applNo;

	public Calendar inputDate;

	public String billingPerson;
	public BigDecimal paymentMethod;
	public Boolean ignoreLock;

}
