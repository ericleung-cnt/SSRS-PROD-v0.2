package org.mardep.ssrs.dmi.dn;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.IDemandNoteService;
import org.mardep.ssrs.service.IDnsService;
import org.mardep.ssrs.service.IInboxService;
import org.mardep.ssrs.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class DemandNoteRefundDMI extends AbstractDMI<DemandNoteRefund>{

	@Autowired
	IDemandNoteService demandNoteService;

	@Autowired
	IDnsService dnsService;
	
	@Autowired
	MailService mailService;

	@Autowired
	IInboxService inboxService;
	
	@Override
	protected IBaseService getBaseService(){
		return demandNoteService;
	}

	@Override
	public DSResponse fetch(DemandNoteRefund entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(DemandNoteRefund entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try{
			String operationId = dsRequest.getOperationId();
			String demandNoteNo = entity.getDemandNoteNo();
			if(DemandNoteOperationId.REFUND_DEMAND_NOTE.equals(operationId)){
				Map clientValues = dsRequest.getClientSuppliedValues();
				Long taskId = (Long)clientValues.get("taskId");
				//DemandNoteRefund refund = demandNoteService.refund(demandNoteNo, entity.getRefundAmount(), entity.getRemarks());
				
				DemandNoteRefund refund = demandNoteService.confirmRecommendRefund(entity);
				dsResponse.setTotalRows(1);
				dsResponse.setData(refund);
				//Asyn
				dnsService.refundDemandNote(refund.getRefundId());
				
				mailService.sendRefund(refund);
				inboxService.proceed(taskId, "confirm", "");
				return dsResponse;
			}
		}catch(Exception ex){
			logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
		return null;
	}

	@Override
	public DSResponse remove(DemandNoteRefund entity, DSRequest dsRequest) {
		return null;
	}

	@Override
	public DSResponse add(DemandNoteRefund entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try{
			String operationId = dsRequest.getOperationId();
			String demandNoteNo = entity.getDemandNoteNo();
			if(DemandNoteOperationId.REFUND_DEMAND_NOTE.equals(operationId)){
				//DemandNoteRefund refund = demandNoteService.refund(demandNoteNo, entity.getRefundAmount(), entity.getRemarks());
				DemandNoteRefund refund = demandNoteService.recommendRefund(demandNoteNo, entity.getRefundAmount(), entity.getRemarks());
				dsResponse.setTotalRows(1);
				dsResponse.setData(refund);
				//Asyn
				//dnsService.refundDemandNote(refund.getRefundId());
				
				//mailService.sendReund(refund);
				inboxService.startWorkflow("dnRefund", demandNoteNo, refund.getId().toString(), "", "");
				return dsResponse;
			}
		}catch(Exception ex){
			logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
		return null;
	}

	public double getRefundAvailability(String demandNoteNo, String refundId) {
		return demandNoteService.getRefundAvailability(demandNoteNo, refundId);
	}
	
}
