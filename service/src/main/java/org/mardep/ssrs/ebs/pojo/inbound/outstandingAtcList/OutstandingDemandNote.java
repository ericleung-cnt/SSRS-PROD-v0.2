package org.mardep.ssrs.ebs.pojo.inbound.outstandingAtcList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class OutstandingDemandNote {
	public String applNo;
	public String vesselName;

}
