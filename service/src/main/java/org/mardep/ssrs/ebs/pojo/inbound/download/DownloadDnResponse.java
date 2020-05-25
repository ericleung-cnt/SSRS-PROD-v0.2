package org.mardep.ssrs.ebs.pojo.inbound.download;

import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.DownloadResponse;
import org.mardep.ssrs.ebs.pojo.EbsNamespace;
@XmlRootElement(name="downloadDnResponse", namespace=EbsNamespace.DEFAULT)
public class DownloadDnResponse extends DownloadResponse {

}
