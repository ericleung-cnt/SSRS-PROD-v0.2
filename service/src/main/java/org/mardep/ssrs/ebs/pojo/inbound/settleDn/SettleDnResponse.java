package org.mardep.ssrs.ebs.pojo.inbound.settleDn;

import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.DownloadResponse;
import org.mardep.ssrs.ebs.pojo.EbsNamespace;

@XmlRootElement(name="settleDnResponse", namespace=EbsNamespace.DEFAULT)
public class SettleDnResponse extends DownloadResponse {

}
