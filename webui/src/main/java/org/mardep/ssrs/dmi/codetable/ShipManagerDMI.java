package org.mardep.ssrs.dmi.codetable;

import java.util.ArrayList;
import java.util.List;

import org.mardep.ssrs.domain.codetable.ShipManager;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ShipManagerDMI extends AbstractCodeTableDMI<ShipManager> {

	@Override
	public DSResponse fetch(ShipManager entity, DSRequest dsRequest){
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		String operationId = dsRequest.getOperationId();
		try{
			if("FETCH_BY_SHIP_MGR_NAME".equalsIgnoreCase(operationId) && entity.getShipMgrName()!=null){
				List<Object> sm = codeTableService.findByCriteria(entity);
				dsResponse.setData(sm);
				return dsResponse;
			} else if ("FETCH_BY_EXACT_SHIP_MGR_NAME".equalsIgnoreCase(operationId) && entity.getShipMgrName()!=null) {
				ShipManager sManager = codeTableService.findShipManagerByShipName(entity.getShipMgrName());
				dsResponse.setData(sManager);
				return dsResponse;
			}
			return super.fetch(entity, dsRequest);
		}catch(Exception ex){
			logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}

	@Override
	public DSResponse update(ShipManager entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(ShipManager entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
