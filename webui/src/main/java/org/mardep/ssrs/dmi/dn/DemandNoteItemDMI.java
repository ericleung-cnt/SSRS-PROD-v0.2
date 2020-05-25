package org.mardep.ssrs.dmi.dn;

import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.IDemandNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class DemandNoteItemDMI extends AbstractDMI<DemandNoteItem>{

	private static final String FETCH_SR_ONLY = "demandNoteItemDS_fetchSrOnly";
	private static final Object FETCH_UNUSED_BY_APPL_NO = "demandNoteItemDS_unused";
	private static final String FETCH_BY_DEMAND_NOTE_NO = "demandNoteItemDS_demandNoteNo";
	
	@Autowired
	IDemandNoteService demandNoteService;

	@Override
	protected IBaseService getBaseService(){
		return demandNoteService;
	}

	@Override
	public DSResponse fetch(DemandNoteItem entity, DSRequest dsRequest){
		if (FETCH_SR_ONLY.equals(dsRequest.getOperationId())) {
			List<DemandNoteItem> items = demandNoteService.findSrDnItems();
			items.forEach(i->{
				i.setFeeCode(null);
				i.setDemandNoteHeader(null);
			});
			return new DSResponse(items, DSResponse.STATUS_SUCCESS);
		} else if (FETCH_UNUSED_BY_APPL_NO.equals(dsRequest.getOperationId())) {
			List<DemandNoteItem> items = demandNoteService.findUnusedByAppl(entity.getApplNo());
			items.forEach(i->{
				i.setFeeCode(null);
				i.setDemandNoteHeader(null);
			});
			return new DSResponse(items, DSResponse.STATUS_SUCCESS);
		} else if (FETCH_BY_DEMAND_NOTE_NO.contentEquals(dsRequest.getOperationId())) {
			List<DemandNoteItem> items = demandNoteService.findByDemandNoteNo(entity.getDnDemandNoteNo());
			DSResponse dsResponse = new DSResponse();
			dsResponse.setData(items);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;
		} else {
			DSResponse dsResponse = super.fetch(entity, dsRequest);
			return dsResponse;
		}
	}

	@Override
	public DSResponse update(DemandNoteItem entity, DSRequest dsRequest) {
		// TODO
		return null;
	}

	@Override
	public DSResponse remove(DemandNoteItem entity, DSRequest dsRequest) {
		String operationId = dsRequest.getOperationId();
		if ("demandNoteItemDS_removeSrItem".equals(operationId)) {
			Map data = dsRequest.getClientSuppliedValues();
			String reason = (String) data.get("reason");
			Long id = (Long) data.get("id");
			if (id != null) {
				demandNoteService.removeItem(id, reason);
			}
			return new DSResponse();
		} else {
			DSResponse dsResponse = new DSResponse();
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;
		}
	}

	@Override
	public DSResponse add(DemandNoteItem entity, DSRequest dsRequest) {
		String operationId = dsRequest.getOperationId();
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			if(DemandNoteOperationId.ADD_DEMAND_NOTE_ITEM.equals(operationId)){
				DemandNoteItem updateItem = demandNoteService.addItem(entity);
				dsResponse.setTotalRows(1);
				dsResponse.setData(updateItem);
			}
			return dsResponse;
		} catch (Exception ex){
			logger.error("Fail to add-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}

//		return super.add(entity, dsRequest);
	}

}
