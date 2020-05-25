package org.mardep.ssrs.ebs.pojo.inbound.shipReg;

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
@XmlRootElement(name="shipRegRequest", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class ShipRegRequest {
	public String vesselName;
	public String officialNo;
	public String imoNo;
}
