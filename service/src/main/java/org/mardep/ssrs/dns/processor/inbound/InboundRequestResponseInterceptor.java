package org.mardep.ssrs.dns.processor.inbound;

import java.util.Date;

import org.mardep.ssrs.dao.dns.ISoapMessageInDao;
import org.mardep.ssrs.dns.IMessagingExceptionService;
import org.mardep.ssrs.dns.pojo.BaseResponse;
import org.mardep.ssrs.dns.processor.AbstractInterceptor;
import org.mardep.ssrs.domain.dns.SoapMessageIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.context.MessageContext;

@Service("inboundRequestResponseInterceptor")
@Transactional
public class InboundRequestResponseInterceptor extends AbstractInterceptor implements IMessagingExceptionService<BaseResponse> {
	
	@Autowired
	protected ISoapMessageInDao soapMessageInDao;
	
	@Override
	public boolean handleInboundRequest(MessageContext messageContext, Object endpoint) throws Exception {
		try {
			logger.info("#handle inbound Request");
			SoapMessageIn soapIn = new SoapMessageIn();
			soapIn.setSent(Boolean.FALSE);
			soapIn.setError(Boolean.FALSE);
			soapIn.setProcessed(Boolean.FALSE);
			soapIn.setRequest(contentToString(messageContext, messageContext.getRequest()));
			SoapMessageIn inResult = soapMessageInDao.save(soapIn);
			messageContext.setProperty(KEY_SOAP_MESSAGE_IN_ID, inResult.getId());
		}catch (Exception e) {
			logger.error("Error on #handleInboundRequest", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean handleInboundResponse(MessageContext messageContext, Object endpoint) throws Exception {
		try {
			logger.info("#handle inbound Response");
			Long soapInId = (Long)messageContext.getProperty(KEY_SOAP_MESSAGE_IN_ID);
			SoapMessageIn soapIn = soapMessageInDao.findById(soapInId);
			soapIn.setSent(Boolean.TRUE);
			soapIn.setSentDate(new Date());
			soapIn.setResponse(contentToString(messageContext, messageContext.getResponse()));
			SoapMessageIn inResult = soapMessageInDao.save(soapIn);
		}catch (Exception e) {
			logger.error("Error on #handleInboundResponse", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean handleInboundFault(MessageContext messageContext, Object endpoint) throws Exception {
		try {
			Long soapInId = (Long)messageContext.getProperty(KEY_SOAP_MESSAGE_IN_ID);
			SoapMessageIn soapIn = soapMessageInDao.findById(soapInId);
			soapIn.setSent(Boolean.TRUE);
			soapIn.setSentDate(new Date());
			soapIn.setError(Boolean.TRUE);
			soapIn.setErrorDate(new Date());
			soapIn.setResponse(contentToString(messageContext, messageContext.getResponse()));
			SoapMessageIn inResult = soapMessageInDao.save(soapIn);

			logger.info("event={}; resultCode={}; error={};", "Inbound", "", "Inbound Fault");
		}catch (Exception e) {
			logger.error("Error on #handleInboundResponse", e);
			return false;
		}
		return true;
	}

	@Override
	@Transactional
	public BaseResponse handleMessagingException(MessagingException e){
		logger.info("receive exception :{}", e.getMessage());
		logger.info("event={}; resultCode={}; error={};", "Inbound", "", "Inbound Exception");
		return null;
	}
	
}
