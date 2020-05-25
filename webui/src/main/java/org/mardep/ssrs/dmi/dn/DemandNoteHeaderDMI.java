package org.mardep.ssrs.dmi.dn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.Rows;
import org.mardep.ssrs.dao.dns.IControlDataDao;
import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.Action;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.domain.dns.ControlAction;
import org.mardep.ssrs.domain.dns.ControlData;
import org.mardep.ssrs.domain.dns.ControlEntity;
import org.mardep.ssrs.report.IDemandNoteGenerator;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.IDemandNoteService;
import org.mardep.ssrs.service.IDnsService;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class DemandNoteHeaderDMI extends AbstractDMI<DemandNoteHeader>{

	@Autowired
	@Qualifier("demandNoteService")
	IDemandNoteService demandNoteService;

	@Autowired
	IDnsService dnsService;

	@Autowired
	IVitalDocClient vitalDocClient;

	@Autowired
	IControlDataDao controlDataDao;

	@Autowired
	@Qualifier("demandNoteGenerator")
	IDemandNoteGenerator demandNoteGenerator;

	@Override
	protected IBaseService getBaseService(){
		return demandNoteService;
	}

	@Override
	public DSResponse fetch(DemandNoteHeader entity, DSRequest dsRequest){
		String operationId = dsRequest.getOperationId();
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try{
			if(DemandNoteOperationId.FIND_DEMAND_NOTE_BY_NO.equals(operationId)){
				DemandNoteHeader header = demandNoteService.enquireDemandNoteDetail(entity.getDemandNoteNo());
				dsResponse.setTotalRows(1);
				dsResponse.setData(header);
				return dsResponse;
			} else if (DemandNoteOperationId.FIND_SR_DEMAND_NOTES.equals(operationId)) {
				dsResponse.setData(demandNoteService.findSrDn(dsRequest.getCriteria(), dsRequest.getStartRow(), dsRequest.getEndRow()));
				dsResponse.setTotalRows(demandNoteService.countSrDn(dsRequest.getCriteria()));
				dsResponse.setStartRow(dsRequest.getStartRow());
				dsRequest.setEndRow(dsRequest.getEndRow());
				return dsResponse;
			} else if ("exportData".equals(operationId)) {
				DSResponse fetchedResp = super.fetch(entity, dsRequest);
				List fetched = fetchedResp.getDataList();
				List<Map<String, Object>> rows = new ArrayList<>();
				for (int i = 0; i < fetched.size(); i++) {
					DemandNoteHeader dnh = (DemandNoteHeader) fetched.get(i);
					LinkedHashMap<String, Object> row = new LinkedHashMap<>();

			         row.put("demandNoteNo",dnh.getDemandNoteNo());
			         row.put("applNo",dnh.getApplNo());
			         row.put("amount",dnh.getAmount());
			         row.put("status",dnh.getStatusStr());
			         row.put("paymentStatus",dnh.getPaymentStatusStr());
			         row.put("generationTime",dnh.getGenerationTime());
			         row.put("dueDate",dnh.getDueDateStr());
			         row.put("billName",dnh.getBillName());
			         row.put("coName",dnh.getCoName());
			         String addr = "";
			         if (dnh.getAddress1() != null) {
			        	 addr += dnh.getAddress1();
			         }
			         if (dnh.getAddress2() != null) {
			        	 if (addr.length() > 0) {
			        		 addr += " ";
			        	 }
			        	 addr += dnh.getAddress2();
			         }
			         if (dnh.getAddress3() != null) {
			        	 if (addr.length() > 0) {
			        		 addr += " ";
			        	 }
			        	 addr += dnh.getAddress3();
			         }
			         row.put("addresses", addr);
			         row.put("shipNameEng",dnh.getShipNameEng());
			         row.put("firstReminderDate",dnh.getFirstReminderDateStr());
			         row.put("secondReminderDate",dnh.getSecondReminderDateStr());

					rows.add(row);
				}
				fetchedResp.setData(rows);
				fetchedResp.setExportFields(Arrays.asList("demandNoteNo, applNo, amount, status, paymentStatus, generationTime, dueDate, billName, coName, shipNameEng, addresses, firstReminderDate, secondReminderDate, ebsFlag".split(", ")));
				return fetchedResp;
			}else{
				return super.fetch(entity, dsRequest);
			}

		}catch(Exception ex){
			logger.error("Fail to fetch-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}

	@Override
	public DSResponse update(DemandNoteHeader entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try{
			String operationId = dsRequest.getOperationId();
			String demandNoteNo = entity.getDemandNoteNo();
			if(DemandNoteOperationId.CANCEL_DEMAND_NOTE.equals(operationId)){
				DemandNoteHeader header = demandNoteService.cancel(demandNoteNo, entity.getCwRemark());
				dsResponse.setTotalRows(1);
				dsResponse.setData(header);

				//Asyn
				dnsService.createDemandNote(demandNoteNo, Action.C, false);

				return dsResponse;
			}else if(DemandNoteOperationId.REFUND_DEMAND_NOTE.equals(operationId)){
				DemandNoteRefund refund = demandNoteService.refund(demandNoteNo, BigDecimal.ZERO, "TODO"); //TODO
				dsResponse.setTotalRows(1);
				dsResponse.setData(refund.getDemandNoteHeader());
				//Asyn
				dnsService.refundDemandNote(refund.getRefundId());

				return dsResponse;
			}else{
				return super.update(entity, dsRequest);
			}
		}catch(Exception ex){
			logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}

	}

	@Override
	public DSResponse add(DemandNoteHeader entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			String operationId = dsRequest.getOperationId();
			if(DemandNoteOperationId.CREATE_AD_HOC_DEMAND_NOTE.equals(operationId)){
				DemandNoteHeader header = null;
				if (entity.getDemandNoteNo() == null) {
					header = demandNoteService.create(entity, false);
					dsResponse.setTotalRows(1);
					dsResponse.setData(header);

					String demandNoteNo = header.getDemandNoteNo();
//					20190806
					dnsService.createDemandNote(demandNoteNo, Action.U, false);

					ControlData controlData = controlDataDao.find(ControlEntity.DN, ControlAction.CREATE, demandNoteNo);

					byte[] osContent = controlData.getFile();
					if(osContent!=null){
						//20190806
						vitalDocClient.uploadDemandNote(demandNoteNo, osContent);
					}
				}
			}else if(DemandNoteOperationId.CREATE_REGULAR_DEMAND_NOTE.equals(operationId)){
//				TODO
			}
			return dsResponse;
		} catch (Exception ex){
			logger.error("Fail to add-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}

}
