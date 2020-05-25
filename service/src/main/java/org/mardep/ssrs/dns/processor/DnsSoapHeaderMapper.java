package org.mardep.ssrs.dns.processor;

import javax.xml.namespace.QName;

import org.mardep.ssrs.dns.pojo.DnsNamespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.ws.DefaultSoapHeaderMapper;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

public class DnsSoapHeaderMapper extends DefaultSoapHeaderMapper {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void populateUserDefinedHeader(String headerName, Object headerValue, SoapMessage target) {
		if (headerValue instanceof String) {
			SoapHeaderElement myHeaderElement = target.getSoapHeader().addHeaderElement(new QName(DnsNamespace.DNS, headerName, DnsNamespace.DNS_PREFIX));
            myHeaderElement.setText(headerValue.toString());
		}
	}
}
