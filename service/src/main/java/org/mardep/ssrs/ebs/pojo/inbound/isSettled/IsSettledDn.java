package org.mardep.ssrs.ebs.pojo.inbound.isSettled;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name="dn")
@XmlAccessorType(XmlAccessType.FIELD)
public class IsSettledDn {
	public String dnNo = "";
	public String status = "";
}
