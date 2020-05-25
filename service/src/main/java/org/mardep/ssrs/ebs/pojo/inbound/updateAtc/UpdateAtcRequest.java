package org.mardep.ssrs.ebs.pojo.inbound.updateAtc;

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
@XmlRootElement(name="updateAtcRequest", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateAtcRequest {
	public String applNo;
	public boolean epaymentIndicator;

}
