package org.mardep.ssrs.dns.processor;

import javax.xml.transform.Source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

public abstract class AbstractSoapMessageProcessor {

	protected final Logger logger=LoggerFactory.getLogger(getClass());
	
	public Source process(SaajSoapMessage soapMessage){
		logger.info("#processSoapMessageRequest:{}", soapMessage);
		 
//		try {
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			soapMessage.getSaajMessage().writeTo(out);
//			String strMsg = new String(out.toByteArray());
//			logger.info("#processSoapMessageRequest:{}", strMsg);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		logger.info("#processSoapMessageRequest:{}", soapMessage.getPayloadSource());
//		TODO persistence to DB;
		return soapMessage.getPayloadSource();
	}

}
