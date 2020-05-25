package org.mardep.ssrs.dns.processor;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.lang.SystemUtils;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.xml.transform.StringResult;

@Transactional
public class AbstractInterceptor implements EndpointInterceptor, ClientInterceptor{

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final static String KEY_SOAP_MESSAGE_IN_ID = "SOAP_MESSAGE_IN_ID";
	protected final static String KEY_SOAP_MESSAGE_OUT_ID = "SOAP_MESSAGE_OUT_ID";
	protected final static String KEY_CONTROL_ID = "controlId";

	@Value(value = "${ssrs.dns.isLogSoapMessage:true}")
	boolean isLogSoapMessage = true;

	/**
	 * login as SYSTEM and log the soap message if it is enabled
	 * @param messageContext
	 * @param wsm
	 */
	protected void logContent(MessageContext messageContext, WebServiceMessage wsm){
		try{
			User user = new User();
			user.setId("SYSTEM");
			user.setUserName("SYSTEM");
			UserContextThreadLocalHolder.setCurrentUser(user);
			if(!isLogSoapMessage) return;
			if(wsm instanceof SaajSoapMessage){
				SaajSoapMessage soapMessage = (SaajSoapMessage)wsm;

				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				StringResult result = new StringResult();
				transformer.transform(soapMessage.getEnvelope().getSource(), result);
				String msg = result.toString();
				String property = System.getProperty(getClass().getName() + ".maxLogSize");
				if (property != null && property.matches("\\d\\d*")) {
					int max = Integer.parseInt(property);
					if (msg.length() > max) {
						msg = msg.substring(0, max) + "...";
					}
				}
				logger.info("{}{}", new Object[]{SystemUtils.LINE_SEPARATOR, msg});
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


	@Override
	public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
		logContent(messageContext, messageContext.getRequest());
		return handleOutboundRequest(messageContext);
	}

	@Override
	public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
		logContent(messageContext, messageContext.getResponse());
		return handleOutboundResponse(messageContext);
	}

	@Override
	public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
		logContent(messageContext, messageContext.getResponse());
		// TODO Auto-generated method stub
		return handleOutboundFault(messageContext);
	}
	@Override
	public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
		afterCompletionOutbound(messageContext, ex);
	}

	public boolean handleOutboundRequest(MessageContext messageContext) throws WebServiceClientException {
		logger.info("Outbound - #afterCompletion:{}", messageContext);
		return true;
	}

	public boolean handleOutboundResponse(MessageContext messageContext) throws WebServiceClientException {
		return true;
	}

	public boolean handleOutboundFault(MessageContext messageContext) throws WebServiceClientException {
		return true;
	}

	public void afterCompletionOutbound(MessageContext messageContext, Exception ex) throws WebServiceClientException {
	}



	@Override
	public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
		logContent(messageContext, messageContext.getRequest());
		return handleInboundRequest(messageContext, endpoint);
	}
	@Override
	public boolean handleResponse(MessageContext messageContext, Object endpoint) throws Exception {
		logContent(messageContext, messageContext.getResponse());
		return handleInboundResponse(messageContext, endpoint);
	}
	@Override
	public boolean handleFault(MessageContext messageContext, Object endpoint) throws Exception {
		// TODO Auto-generated method stub
		return handleInboundFault(messageContext, endpoint);
	}
	@Override
	public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) throws Exception {
		afterCompletionInbound(messageContext, endpoint, ex);
	}

	public boolean handleInboundRequest(MessageContext messageContext, Object endpoint) throws Exception {
		return true;
	}

	public boolean handleInboundResponse(MessageContext messageContext, Object endpoint) throws Exception {
		return true;
	}

	public boolean handleInboundFault(MessageContext messageContext, Object endpoint) throws Exception {
		return true;
	}

	public void afterCompletionInbound(MessageContext messageContext, Object endpoint, Exception ex) throws Exception {
	}

}
