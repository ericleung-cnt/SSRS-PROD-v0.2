package org.mardep.ssrs.dmi.seafarer;

import java.util.ArrayList;
import java.util.List;

import org.mardep.ssrs.domain.seafarer.SeaService;
import org.mardep.ssrs.service.ISeafarerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SeaServiceDMI extends AbstractSeafarerDMI<SeaService> {

	@Autowired
	ISeafarerService seafarerService;
	
	@Override
	public DSResponse fetch(SeaService entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(SeaService seaService, DSRequest dsRequest) throws Exception {
		String operationId = dsRequest.getOperationId();
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try{
			if("CAN_ADD".equalsIgnoreCase(operationId)){
				boolean canAdd = seafarerService.canAdd(seaService);
				return dsResponse;
			}else{
				seaService.setSeafarer(null);
				seaService.setShipType(null);
				seafarerService.canUpdate(seaService);
				return super.update(seaService, dsRequest);
			}
			
		}catch(Exception ex){
			logger.error("Fail to add-{}, Exception-{}", new Object[]{seaService, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}
	
	@Override
	public DSResponse add(SeaService seaService, DSRequest dsRequest) throws Exception {
		String operationId = dsRequest.getOperationId();
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			if("CAN_ADD".equalsIgnoreCase(operationId)){
				boolean canAdd = seafarerService.canAdd(seaService);
				return dsResponse;
			}else{
				seaService.setSeafarer(null);
				seaService.setShipType(null);
				SeaService dbSeaService = seafarerService.add(seaService);
				List<SeaService> list = new ArrayList<SeaService>();
				list.add(dbSeaService);
				dsResponse.setData(list);
				dsResponse.setTotalRows(1);
				return dsResponse;
			}
		}catch(Exception ex){
			logger.error("Fail to add-{}, Exception-{}", new Object[]{seaService, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}
	
	@Override
	public DSResponse remove(SeaService entity, DSRequest dsRequest) throws Exception {
		return super.remove(entity, dsRequest);
	}
	
}
