package org.mardep.ssrs.ebs.pojo.inbound.outstandingAtcList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.EbsNamespace;

import lombok.Data;

@Data
@XmlRootElement(name="outstandingAtcListRequest", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class OutstandingAtcListRequest {

}
