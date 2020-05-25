package org.mardep.ssrs.ebs;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.xml.transform.StringResult;

public class LogClientInterceptor implements ClientInterceptor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public LogClientInterceptor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
		logContent(messageContext, messageContext.getRequest());
		return true;
	}

	@Override
	public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
		logContent(messageContext, messageContext.getRequest());
		return true;
	}

	@Override
	public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
		logContent(messageContext, messageContext.getRequest());
		return true;
	}

	@Override
	public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
		logContent(messageContext, messageContext.getRequest());
	}
	
	protected void logContent(MessageContext messageContext, WebServiceMessage wsm){
		try{
			if(wsm instanceof SaajSoapMessage){
				SaajSoapMessage soapMessage = (SaajSoapMessage)wsm;
				
//				ByteArrayOutputStream baos= new ByteArrayOutputStream();
//				soapMessage.getSaajMessage().writeTo(baos);
//				String a = new String(baos.toByteArray());
//				logger.info("XML:{}{}",new Object[]{SystemUtils.LINE_SEPARATOR, a});
				
				
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				StringResult result = new StringResult();
				transformer.transform(soapMessage.getEnvelope().getSource(), result);
				logger.info("{}{}", new Object[]{SystemUtils.LINE_SEPARATOR, result});
			}
		}catch(Exception ex){
			logger.error("#logContent", ex);
		}
	}
	
	protected String contentToString(MessageContext messageContext, WebServiceMessage wsm){
		try{
			if(wsm instanceof SaajSoapMessage){
				SaajSoapMessage soapMessage = (SaajSoapMessage)wsm;
				
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				StringResult result = new StringResult();
				transformer.transform(soapMessage.getEnvelope().getSource(), result);
				return result.toString();
			}
		}catch(Exception ex){
			logger.error("#logContent", ex);
		}
		return null;
	}
	

}
