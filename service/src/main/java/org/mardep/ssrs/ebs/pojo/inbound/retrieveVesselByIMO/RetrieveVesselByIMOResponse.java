package org.mardep.ssrs.ebs.pojo.inbound.retrieveVesselByIMO;

import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.BaseResponse;
import org.mardep.ssrs.ebs.pojo.EbsNamespace;
import org.mardep.ssrs.ebs.pojo.VesselListResponse;

@XmlRootElement(name="retrieveVesselByIMOResponse", namespace=EbsNamespace.DEFAULT)
public class RetrieveVesselByIMOResponse extends VesselListResponse {

}
