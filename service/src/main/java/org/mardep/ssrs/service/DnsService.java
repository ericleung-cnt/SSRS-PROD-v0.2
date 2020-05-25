package org.mardep.ssrs.service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.xml.transform.Source;

import org.apache.commons.lang.time.DateUtils;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.dao.dn.IDemandNoteRefundDao;
import org.mardep.ssrs.dao.dns.IControlDataDao;
import org.mardep.ssrs.dao.dns.ISoapMessageInDao;
import org.mardep.ssrs.dao.dns.ISoapMessageOutDao;
import org.mardep.ssrs.dns.IDnsOutService;
import org.mardep.ssrs.dns.pojo.BaseResponse;
import org.mardep.ssrs.dns.pojo.BaseResult;
import org.mardep.ssrs.dns.pojo.BaseReturnResult;
import org.mardep.ssrs.dns.pojo.common.CreateReceiptAction;
import org.mardep.ssrs.dns.pojo.common.ReceiptItem;
import org.mardep.ssrs.dns.pojo.outbound.RequestFile;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.Action;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteInfo;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteItem;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteRequest;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteResponse;
import org.mardep.ssrs.dns.pojo.outbound.createReceipt.CreateReceiptInfo;
import org.mardep.ssrs.dns.pojo.outbound.createReceipt.ReceiptRequest;
import org.mardep.ssrs.dns.pojo.outbound.createReceipt.ReceiptResponse;
import org.mardep.ssrs.dns.pojo.outbound.refund.RefundInfo;
import org.mardep.ssrs.dns.pojo.outbound.refund.RefundRequest;
import org.mardep.ssrs.dns.pojo.outbound.refund.RefundResponse;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.domain.dns.ControlAction;
import org.mardep.ssrs.domain.dns.ControlData;
import org.mardep.ssrs.domain.dns.ControlEntity;
import org.mardep.ssrs.domain.dns.SoapMessageOut;
import org.mardep.ssrs.report.IReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;

@Service
@Transactional
public class DnsService extends AbstractService implements IDnsService{

	@Autowired
	IControlDataDao controlDataDao;

	@Autowired
	ISoapMessageOutDao soapMessageOutDao;

	@Autowired
	ISoapMessageInDao soapMessageInDao;

	@Autowired
	IDnsOutService dnsOutService;

	@Autowired
	@Qualifier("dnsMessageFactory")
	SoapMessageFactory soapMessageFactory;

	@Autowired
	IDemandNoteHeaderDao demandNoteHeaderDao;

	@Autowired
	IDemandNoteRefundDao demandNoteRefundDao;

	@Autowired
	IDemandNoteItemDao demandNoteItemDao;

	@Autowired
	@Qualifier("demandNoteGenerator")
	IReportGenerator demandNoteGenerator;

	@Override
	public ControlData create(String demandNoteNo, ControlEntity controlEntity, ControlAction controlAction){
		ControlData controlData = new ControlData();
		controlData.setEntity(controlEntity.getCode());
		controlData.setAction(controlAction.getCode());
		controlData.setEntityId(demandNoteNo);
		if(ControlAction.CREATE.equals(controlAction)){
			try {
				Map<String, Object> inputParam = new HashMap<String, Object>();
				inputParam.put("demandNoteNo", demandNoteNo);
				byte[] dn = demandNoteGenerator.generate(inputParam);
				controlData.setFile(dn);
//				controlData.setFileRequired(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//For Debuging
		ControlData newCD = controlDataDao.save(controlData);



		try{
			switch(controlEntity){
			case DN:
				handleDemandNote(newCD, controlAction, demandNoteNo);
				break;
			case RECEIPT:
//				TODO
				break;
			default:
				logger.warn("DNS Service, should be be here");
				break;
			}
		}catch(Exception ex){
			logger.error("Error on sending message to DNS!", ex);
		}

		return newCD;
	}

	protected void handleDemandNote(ControlData controlData, ControlAction ca, String dnId){
		switch(ca){
			case CANCEL:
//				createDemandNote(controlData, dnId, Action.C);
				break;
			case CREATE:
//				createDemandNote(controlData, dnId, Action.U);
				break;
			case UPDATE:
//				createDemandNote(controlData, dnId, Action.R);
				logger.warn("Update DemandNote is not support!");
				break;
			case VALUE_DATE:
				logger.warn("Value DemandNote is not support!");
//				createDemandNote(controlData, dnId, Action.R);
				break;
			case REFUND:
//				refundDemandNote(controlData, dnId);
				break;
		}
	}

	@Async
	@Override
	public void refundDemandNote(Long refundId) {
		DemandNoteRefund demandNoteRefund = demandNoteRefundDao.findById(refundId);
		if(demandNoteRefund==null) {
			logger.warn("DemandNoteRefund not found for refund:{}", refundId);
			return;
		}
		ControlData controlData = controlDataDao.find(ControlEntity.REFUND, ControlAction.REFUND, refundId.toString());
		if(controlData==null) {
			logger.warn("ControlData not found for refund:{}", refundId);
			return;
		}

		String demandNoteNo = demandNoteRefund.getDemandNoteNo();

		RefundInfo info = new RefundInfo();
		info.setDnNo(demandNoteNo);
		info.setBillCode(Cons.DNS_BILL_CODE);
		info.setRemarks(demandNoteRefund.getRemarks());
		info.setRefundAmount(demandNoteRefund.getRefundAmount());
		info.setUserCode(demandNoteRefund.getUpdatedBy());
//		if (demandNoteNo != null && demandNoteNo.length() >= 4) {
//			info.setOfficeCode(demandNoteNo.substring(2,4));
//		}
		info.setOfficeCode("12"); // "12" is the office code of Finance HQ 20191011
		info.setRefId(demandNoteRefund.getId().toString());
//		refundInfo.setItemNo(itemNo);
//		refundInfo.setParticular(particular);
//		refundInfo.setFeeCode(feeCode);
		RefundRequest  request = new RefundRequest();
		request.setRefundInfo(info);
		request.setControlId(controlData.getId().toString());
		try{
			RefundResponse result = dnsOutService.sendRefund(request);
			if(result!=null){
//				if()
				BaseResult br = result.getBaseResult();
				if(br!=null){
					BaseReturnResult baseReturnResult = br.getBaseReturnResult();
					if(baseReturnResult!=null){
						String resultMsg = baseReturnResult.getResult();
						String resultDescription = baseReturnResult.getDescription();

						demandNoteRefund.setDnsResult(resultMsg);
						demandNoteRefund.setDnsDescription(resultDescription);
						demandNoteRefund.setUpdatedBy("DNS");
						demandNoteRefund.setUpdatedDate(new Date());
						demandNoteRefundDao.save(demandNoteRefund);
					}
				}
//				getBaseReturnResult().getResult();
			}
		}catch(Exception ex){
			logger.error("Fail to send Refund Request", ex);
		}

	}

	@Async("dnsAsnycExecutor")
	@Override
	public void createDemandNote(String demandNoteNo, Action action, boolean autopay) {
		try {
			ControlAction ca = Action.C == action ? ControlAction.CANCEL: ControlAction.CREATE;
			ControlData controlData = controlDataDao.find(ControlEntity.DN, ca, demandNoteNo);
			DemandNoteHeader dnHeader = demandNoteHeaderDao.findById(demandNoteNo);
			List<org.mardep.ssrs.domain.dn.DemandNoteItem> demandNoteItemList = demandNoteItemDao.findByDemandNoteNo(demandNoteNo);
			DemandNoteRequest request = new DemandNoteRequest();
			//Header info
			request.setAction(action);
			request.setControlId(controlData.getId().toString());
			RequestFile rf = new RequestFile();
			rf.setdFile(controlData.getFile());
			request.setFile(rf);

			//Body Info
	    	DemandNoteInfo info = new DemandNoteInfo();
	    	info.setDnNo((demandNoteNo+ "               ").substring(0, 15));
	    	info.setLastUpdateDatetime(dnHeader.getUpdatedDate());
	    	info.setUserCode(dnHeader.getUpdatedBy());
	    	info.setIssueDate(DateUtils.truncate(dnHeader.getGenerationTime(), Calendar.DATE));
	    	info.setAmountTTL(dnHeader.getAmount());
	    	info.setBillCode(Cons.DNS_BILL_CODE);
	    	//String officeCode = dnHeader.getApplNo() != null && dnHeader.getApplNo().length() > 1 ? Cons.SSRS_SR_OFFICE_CODE : Cons.SSRS_MMO_OFFICE_CODE;
	    	String officeCode = demandNoteNo.substring(2,4);
	    	info.setOfficeCode(officeCode);
	    	info.setPayerName(dnHeader.getBillName() != null && dnHeader.getBillName().length() > 120 ? dnHeader.getBillName().substring(0,  120) : dnHeader.getBillName());
	    	info.setRemarks(dnHeader.getCwRemark());
	    	info.setAutopayRequest(autopay ? 1 : 0);
	    	info.setIsAutopay(autopay ? 1 : 0);
	    	info.setDueDate(dnHeader.getDueDate());
	    	info.setDnStatus(action == Action.C ? 12 : 3);
	    	//Charge Items
	    	List<DemandNoteItem> itemList = new ArrayList<DemandNoteItem>();
	    	int[] idx = {0};
	    	demandNoteItemList.forEach(dni -> {
	    		idx[0]++;
	    		itemList.add(construct(idx[0], dni));
	    	});

	    	info.setItemList(itemList);
	    	request.setDemandNoteInfo(info);
	    	DemandNoteResponse result = dnsOutService.sendDemandNote(request);
	    	if (result != null) {
	    		BaseResult baseResult = result.getBaseResult();
	    		if (baseResult != null) {
	    			BaseReturnResult baseReturnResult = baseResult.getBaseReturnResult();
	    			if (baseReturnResult != null) {
	    				logger.info("DemandNoteResponse:--{},--{},--{},--{},--{},--{}",
	    						baseResult.getDnNo(),
	    						baseResult.getReceiptNo(),
	    						baseResult.getRefId(),
	    						baseReturnResult.getDescription(),
	    						baseReturnResult.getResult(),
	    						baseReturnResult.getResultCode());

	    			} else {
	    				logger.warn("DemandNoteResponse:--{},--{},--{},--null",
	    						baseResult.getDnNo(),
	    						baseResult.getReceiptNo(),
	    						baseResult.getRefId());
	    			}
	    			DemandNoteHeader dnh = demandNoteHeaderDao.findById(baseResult.getDnNo());
	    			if (Cons.CW_STATUS_C.equals(dnh.getCwStatus())) {
	    				dnh.setStatus(DemandNoteHeader.STATUS_CANCELLED);
	    			}
	    		} else {
    				logger.warn("DemandNoteResponse:--null");
	    		}
	    	} else {
				logger.warn("dnsOutService.sendDemandNote(request) returns null");
	    	}
		} catch (MessagingException me) {
			logger.error("org.mardep.ssrs.service.DnsService.createDemandNote failed to invoke dns: " + me.getCause().getMessage());
		}catch (Exception e) {
			logger.error("error at createDemandNote", e);
		}
	}

	private DemandNoteItem construct(int idx, org.mardep.ssrs.domain.dn.DemandNoteItem dni){
		DemandNoteItem item = new DemandNoteItem();
    	item.setItemNo(idx);
    	item.setIsRemark(0);
    	if(dni.getFcFeeCode()!=null && dni.getFeeCode()!=null){
    		FeeCode feeCode = dni.getFeeCode();
    		item.setParticular(feeCode.getEngDesc());
    		item.setRevenueType(feeCode.getFormCode());
    		item.setFeeCode(feeCode.getId());
    		item.setUnitPrice(feeCode.getFeePrice());
    	}
    	item.setUnit(dni.getChargedUnits());
    	item.setAmount(dni.getAmount());
    	return item;
	}


//	@Override
	public void resendFailSoapOut(Long soapOutId) {
		try {
			logger.info("#resendFailSoapOut, soapOutId:{}", soapOutId);
			SoapMessageOut out = soapMessageOutDao.findById(soapOutId);
			logger.info("SoapMessageOut:{}", out.getId());
			String requestMessage = out.getRequest();
			SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage(new ByteArrayInputStream(requestMessage.getBytes()));
			SoapBody body = soapMessage.getSoapBody();
			Source source = body.getPayloadSource();
			source.setSystemId(out.getControlId().toString());
			BaseResponse baseResponse = dnsOutService.resendFailSoap(source);
			logger.info("BaseResponse:{}", baseResponse);
		} catch (Exception e) {
			logger.error("error at resendFailSoapOut", e);
		}
	}

	@Async("dnsAsnycExecutor")
	@Override
	public void updatePayment(String receiptNo, String dnNo, BigDecimal amount, Date receiptDate, String paymentType) {
		sendPayment(receiptNo, dnNo, amount, receiptDate, paymentType, CreateReceiptAction.U);
	}

	@Async("dnsAsnycExecutor")
	@Override
	public void cancelPayment(String receiptNo, String dnNo, BigDecimal amount, Date receiptDate, String paymentType) {
		sendPayment(receiptNo, dnNo, amount, receiptDate, paymentType, CreateReceiptAction.C);
	}

	private void sendPayment(String receiptNo, String dnNo, BigDecimal amount, Date receiptDate, String paymentType, CreateReceiptAction action) {
		ControlData cd = new ControlData();
		cd.setEntity(ControlEntity.RECEIPT.getCode());
		cd.setAction(action == CreateReceiptAction.U ? ControlAction.CREATE.getCode() : ControlAction.CANCEL.getCode());
		cd.setEntityId(receiptNo);
		cd = controlDataDao.save(cd);
		ReceiptRequest req = new ReceiptRequest();
		req.setControlId(cd.getId().toString());

		req.setCreateReceiptAction(action);
		CreateReceiptInfo info = new CreateReceiptInfo();
		info.setBillCode("05"); // SSRS
		info.setDnNo(dnNo);
		info.setMachineID("");
		ArrayList<ReceiptItem> paymentList = new ArrayList<>();
		ReceiptItem item = new ReceiptItem();
		item.setPaymentAmount(amount);
		item.setPaymentType(paymentType);
		paymentList.add(item);
		info.setPaymentList(paymentList);
		info.setReceiptAmount(amount);
		info.setReceiptDate(receiptDate);
		info.setReceiptNo(receiptNo);
		req.setReceiptInfo(info);
		ReceiptResponse response = dnsOutService.sendReceipt(req);
		logger.info("update payment {}",response);
	}

}
