package org.mardep.ssrs.dmi.seafarer;

import java.util.ArrayList;
import java.util.List;

import org.mardep.ssrs.domain.seafarer.Employment;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class EmploymentDMI extends AbstractSeafarerDMI<Employment> {

	@Override
	public DSResponse fetch(Employment entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Employment employment, DSRequest dsRequest) throws Exception {
		DSResponse dsResponse = new DSResponse();
		try{
			employment.setSeafarer(null);
			Employment dbEmployment = seafarerService.update(employment);
			List<Employment> list = new ArrayList<Employment>();
			list.add(dbEmployment);
			dsResponse.setData(list);
			dsResponse.setTotalRows(1);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;
		}catch(Exception ex){
			logger.error("Fail to add-{}, Exception-{}", new Object[]{employment, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}
	
	@Override
	public DSResponse add(Employment employment, DSRequest dsRequest) throws Exception {
		DSResponse dsResponse = new DSResponse();
		try{
			Employment dbEmployment = seafarerService.add(employment);
			List<Employment> list = new ArrayList<Employment>();
			list.add(dbEmployment);
			dsResponse.setData(list);
			dsResponse.setTotalRows(1);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;
		}catch(Exception ex){
			logger.error("Fail to add-{}, Exception-{}", new Object[]{employment, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}
	
	@Override
	public DSResponse remove(Employment entity, DSRequest dsRequest) throws Exception {
		return super.remove(entity, dsRequest);
	}
}
