package org.mardep.ssrs.ebs.pojo.inbound.download;

import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.DownloadResponse;
import org.mardep.ssrs.ebs.pojo.EbsNamespace;

@XmlRootElement(name="downloadTranscriptResponse", namespace=EbsNamespace.DEFAULT)
public class DownloadTranscriptResponse extends DownloadResponse {

}
