package org.mardep.ssrs.ebs.pojo.inbound.getTakeUpRateStat;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.EbsNamespace;

import lombok.Data;

@Data
@XmlRootElement(name="getTakeUpRateStatRequest", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTakeUpRateStatRequest {
	public Calendar fromDate;
	public Calendar toDate;
}
