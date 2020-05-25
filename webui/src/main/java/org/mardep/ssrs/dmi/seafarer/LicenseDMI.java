package org.mardep.ssrs.dmi.seafarer;

import java.util.ArrayList;
import java.util.List;

import org.mardep.ssrs.domain.seafarer.License;
import org.mardep.ssrs.service.ISeafarerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class LicenseDMI extends AbstractSeafarerDMI<License> {

	@Autowired
	ISeafarerService seafarerService;
	
	@Override
	public DSResponse fetch(License entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(License entity, DSRequest dsRequest) throws Exception {
		logger.info("#Update Training Course");
		DSResponse dsResponse = new DSResponse(DSResponse.STATUS_SUCCESS);
		try{
			entity.setSeafarer(null);
			License updateLicense = seafarerService.update(entity);
			List<License> list = new ArrayList<License>();
			list.add(updateLicense);
			dsResponse.setTotalRows(1);
			dsResponse.setData(list);
			return dsResponse;
		}catch(Exception ex){
			logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}
	
	@Override
	public DSResponse add(License entity, DSRequest dsRequest) throws Exception {
		logger.info("#Add Training Course");
		DSResponse dsResponse = new DSResponse(DSResponse.STATUS_SUCCESS);
		try{
			entity.setSeafarer(null);
			List<License> list = new ArrayList<License>();
			synchronized(this){
				License updateLicense = seafarerService.add(entity);
				list.add(updateLicense);
			}
			dsResponse.setTotalRows(1);
			dsResponse.setData(list);
			return dsResponse;
		}catch(Exception ex){
			logger.error("Fail to add-{}, Exception-{}", new Object[]{entity, ex}, ex);
//			return handleException(dsResponse, ex);
			List<String> errors = new ArrayList<String>();
			errors.add(ex.getMessage());
			dsResponse.setErrors(errors);
			dsResponse.setFailure();
			return dsResponse;
		}
	}
	
	@Override
	public DSResponse remove(License entity, DSRequest dsRequest) throws Exception {
		return super.remove(entity, dsRequest);
	}
	
}
