package org.mardep.ssrs.dns.processor.outbound;

import java.net.SocketTimeoutException;
import java.util.Date;

import javax.xml.namespace.QName;

import org.mardep.ssrs.dao.dns.IControlDataDao;
import org.mardep.ssrs.dao.dns.ISoapMessageOutDao;
import org.mardep.ssrs.dns.IMessagingExceptionService;
import org.mardep.ssrs.dns.pojo.BaseResponse;
import org.mardep.ssrs.dns.pojo.BaseResult;
import org.mardep.ssrs.dns.pojo.DnsNamespace;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteRequest;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteResponse;
import org.mardep.ssrs.dns.pojo.outbound.createReceipt.ReceiptRequest;
import org.mardep.ssrs.dns.pojo.outbound.createReceipt.ReceiptResponse;
import org.mardep.ssrs.dns.processor.AbstractInterceptor;
import org.mardep.ssrs.domain.dns.ControlData;
import org.mardep.ssrs.domain.dns.SoapMessageOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.handler.ReplyRequiredException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

/**
 *
 * Persist to DB
 *
 *
 */
@Service("outboundRequestResponseInterceptor")
public class OutboundRequestResponseInterceptor extends AbstractInterceptor implements IMessagingExceptionService<BaseResponse> {

	@Autowired
	protected ISoapMessageOutDao soapMessageOutDao;
	@Autowired
	protected IControlDataDao controlDataDao;

	/**
	 *
	 * TODO only handle for the first time while retry occur
	 *
	 */
	@Override
	public boolean handleOutboundRequest(MessageContext messageContext) throws WebServiceClientException {
		try {
			logger.info("#handle outbound Request");
			int retryCount = RetrySynchronizationManager.getContext().getRetryCount();
			logger.info("retry count:{}", retryCount);
			if(retryCount>0) {
				return true;
			}
//			QName controlIdQName = new QName("controlId");
//			SoapHeader soapHeader = ((SaajSoapMessage)messageContext.getRequest()).getSoapHeader();
//			String controlIdString = soapHeader.getAttributeValue(controlIdQName);
//			soapHeader.removeAttribute(controlIdQName);

			QName controlIdQName = new QName(DnsNamespace.DNS, KEY_CONTROL_ID, DnsNamespace.DNS_PREFIX);
			SoapHeader soapHeader = ((SaajSoapMessage)messageContext.getRequest()).getSoapHeader();
			String controlIdString = soapHeader.examineHeaderElements(controlIdQName).next().getText();
			soapHeader.removeHeaderElement(controlIdQName);

			Long controlId = controlIdString == null ? null : Long.valueOf(controlIdString);
			logger.info("controlId = {}", controlId);
			messageContext.setProperty(KEY_CONTROL_ID, controlId);

//			SoapMessageOut dbSoapOut = soapMessageOutDao.findByControlId(controlId);
//			if(dbSoapOut==null){
//				dbSoapOut = new SoapMessageOut();
//			}
			SoapMessageOut dbSoapOut = new SoapMessageOut();
			dbSoapOut.setError(Boolean.FALSE);
			dbSoapOut.setProcessed(Boolean.TRUE);
			dbSoapOut.setProcessedDate(new Date());
			dbSoapOut.setRequest(contentToString(messageContext, messageContext.getRequest()));

			dbSoapOut.setControlId(controlId);
			SoapMessageOut newOut = soapMessageOutDao.save(dbSoapOut);

			ControlData c = controlDataDao.findById(controlId);
			c.setProcessed(true);
			c.setProcessedDate(new Date());
			controlDataDao.save(c);

			messageContext.setProperty(KEY_SOAP_MESSAGE_OUT_ID, newOut.getId());
		}catch (Exception e) {
			logger.error("Error on #handleOutboundRequest", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean handleOutboundResponse(MessageContext messageContext) throws WebServiceClientException {
		try {
			logger.info("#handle outbound Response");
			Long outId = Long.valueOf(messageContext.getProperty(KEY_SOAP_MESSAGE_OUT_ID).toString());
			SoapMessageOut soapOut = soapMessageOutDao.findById(outId);
			String responseStr = contentToString(messageContext, messageContext.getResponse());
			soapOut.setResponse(responseStr);

			int resultBegin = responseStr.indexOf("<result>");
			int resultEnd = responseStr.indexOf("</result>");
			if (resultBegin >= 0){
				String resultString = responseStr.substring(resultBegin+"<result>".length(), resultEnd);
				int descBegin = responseStr.indexOf("<description>");
				int descEnd = responseStr.indexOf("</description>");
				if (descBegin >= 0){
					String errorDesc = responseStr.substring(descBegin+"<description>".length(), descEnd);
					if (resultString != null && !"00000".equals(resultString)){
						soapOut.setError(true);
						soapOut.setErrorDate(new Date());
						logger.info("event={}; resultCode={}; error={};", resultString, resultString, errorDesc);
					}
				}
			}

			soapMessageOutDao.save(soapOut);
		}catch (Exception e) {
			logger.error("Error on #handleOutboundResponse", e);
			return false;
		}
		return true;
 	}

	@Override
	public boolean handleOutboundFault(MessageContext messageContext) throws WebServiceClientException {
		try {
			logger.info("#handle outbound Fault");
			Long outId = Long.valueOf(messageContext.getProperty(KEY_SOAP_MESSAGE_OUT_ID).toString());
			SoapMessageOut soapOut = soapMessageOutDao.findById(outId);
			String responseStr = contentToString(messageContext, messageContext.getResponse());
			soapOut.setResponse(responseStr);
			soapOut.setError(Boolean.TRUE);
			soapOut.setErrorDate(new Date());
			soapMessageOutDao.save(soapOut);

			int faultBegin = responseStr.indexOf("<faultstring>");
			int faultEnd = responseStr.indexOf("</faultstring>");
			String faultstring = responseStr.substring(faultBegin+"<faultstring>".length(), faultEnd);
			int codeBegin = responseStr.indexOf("<code>");
			int codeEnd = responseStr.indexOf("</code>");
			String errorCode = responseStr.substring(codeBegin+"<code>".length(), codeEnd);
			int descBegin = responseStr.indexOf("<description>");
			int descEnd = responseStr.indexOf("</description>");
			String errorDesc = responseStr.substring(descBegin+"<description>".length(), descEnd);

			logger.info("event={}; resultCode={}; error={};", faultstring, errorCode, errorDesc);

//			QName controlIdQName = new QName("controlId");
//			SoapHeader soapHeader = ((SaajSoapMessage)messageContext.getRequest()).getSoapHeader();
//			String controlIdString = soapHeader.getAttributeValue(controlIdQName);
//			Long controlId = Long.valueOf(controlIdString);
			Long controlId = Long.valueOf(messageContext.getProperty(KEY_SOAP_MESSAGE_OUT_ID).toString());
			ControlData c = controlDataDao.findById(controlId);
			c.setError(true);
			c.setErrorDate(new Date());
			controlDataDao.save(c);
		}catch (Exception e) {
			logger.error("Error on #handleOutboundFault", e);
		}
		return true;
	}

	/**
	 *
	 * TODO handle
	 *
	 */
	@Override
	@Transactional
	public BaseResponse handleMessagingException(MessagingException e){
		logger.info("outbound receive exception :{}", e.getMessage());
		logger.info("event={}; resultCode={}; error={};", "Outbound", "", "Outbound Exception");
		BaseResponse result = null;
		BaseResult baseResult = new BaseResult();
		Message<?> message = ((MessagingException)e).getFailedMessage();
		Object o = message.getPayload();
		if(o instanceof DemandNoteRequest){
			result = new DemandNoteResponse();
		}else if(o instanceof ReceiptRequest){
			result = new ReceiptResponse();
		}
		Throwable cause = e.getCause();
		if(cause instanceof SoapFaultClientException){
//			no need to handle as #handleOutboundFault
		}else{
			Long controlId = Long.valueOf(message.getHeaders().get(KEY_CONTROL_ID).toString());
			SoapMessageOut out = soapMessageOutDao.findByControlId(controlId);
			Throwable cause2  = cause.getCause();
			logger.info("Cause:{}", cause.getMessage());
			if(cause2 instanceof ReplyRequiredException || cause2 instanceof SocketTimeoutException){
				out.setTimeout(new Date());
			}else{
				out.setError(Boolean.TRUE);
				out.setErrorDate(new Date());
			}
			soapMessageOutDao.save(out);

			ControlData c = controlDataDao.findById(controlId);
			c.setError(true);
			c.setErrorDate(new Date());
			controlDataDao.save(c);
		}
		result.setBaseResult(baseResult);
		return result;
	}

}
