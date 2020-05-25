package org.mardep.ssrs.ebs.pojo.inbound.updateAtc;

import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.DownloadResponse;
import org.mardep.ssrs.ebs.pojo.EbsNamespace;
@XmlRootElement(name="updateAtcResponse", namespace=EbsNamespace.DEFAULT)
public class UpdateAtcResponse extends DownloadResponse {

}
