package org.mardep.ssrs.ebs.processor;

import javax.xml.namespace.QName;

import org.mardep.ssrs.ebs.pojo.EbsNamespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.ws.DefaultSoapHeaderMapper;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

public class EbsSoapHeaderMapper extends DefaultSoapHeaderMapper {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void populateUserDefinedHeader(String headerName, Object headerValue, SoapMessage target) {
		if (headerValue instanceof String) {
			SoapHeaderElement myHeaderElement = target.getSoapHeader().addHeaderElement(new QName(EbsNamespace.EBS, headerName, EbsNamespace.EBS_PREFIX));
            myHeaderElement.setText(headerValue.toString());
		}
	}
}
