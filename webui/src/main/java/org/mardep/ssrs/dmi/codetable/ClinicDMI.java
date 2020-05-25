package org.mardep.ssrs.dmi.codetable;

import java.util.List;

import org.mardep.ssrs.dao.codetable.IClinicDao;
import org.mardep.ssrs.domain.codetable.Clinic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ClinicDMI extends AbstractCodeTableDMI<Clinic> {

	@Autowired
	IClinicDao clinicDao;
	
	@Override
	public DSResponse fetch(Clinic entity, DSRequest dsRequest){
		if("FIND_ENABLED".equalsIgnoreCase(dsRequest.getOperationId())){
			DSResponse dsResponse = new DSResponse();
			try{
				List<Clinic> findEnabled = clinicDao.findEnabled();
				dsResponse.setTotalRows(findEnabled.size());
				dsResponse.setData(findEnabled);
				dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
				return dsResponse;
				
			}catch(Exception ex){
				logger.error("Fail to Fetch-{}, Exception-{}", new Object[]{entity, ex}, ex);
				return handleException(dsResponse, ex);
			}
		}
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Clinic entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Clinic entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
	@Override
	public DSResponse remove(Clinic entity, DSRequest dsRequest) throws Exception {
		return super.remove(entity, dsRequest);
	}
}
