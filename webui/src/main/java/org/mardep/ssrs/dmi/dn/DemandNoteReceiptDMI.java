package org.mardep.ssrs.dmi.dn;

import java.util.List;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.IDemandNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class DemandNoteReceiptDMI extends AbstractDMI<DemandNoteReceipt>{

	@Autowired
	IDemandNoteService demandNoteService;

	@Override
	protected IBaseService getBaseService(){
		return demandNoteService;
	}

	@Override
	public DSResponse fetch(DemandNoteReceipt entity, DSRequest dsRequest){
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try{
			if ("FIND_VALUE_RECEIPT_BY_NO".equals(dsRequest.getOperationId())) {
				List<DemandNoteReceipt> receipts = demandNoteService.findValue(entity.getDemandNoteNo());
				dsResponse.setTotalRows(receipts.size());
				dsResponse.setData(receipts);
				return dsResponse;
			}
		}catch(Exception ex){
			logger.error("Fail to fetch-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(DemandNoteReceipt entity, DSRequest dsRequest) {
		return null;
	}

	@Override
	public DSResponse remove(DemandNoteReceipt entity, DSRequest dsRequest) {
		return null;
	}

	@Override
	public DSResponse add(DemandNoteReceipt entity, DSRequest dsRequest) {
		return null;
	}

}
