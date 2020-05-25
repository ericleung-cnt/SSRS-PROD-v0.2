package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.PreviousSerb;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class PreviousSerbDMI extends AbstractSeafarerDMI<PreviousSerb> {

	@Override
	public DSResponse fetch(PreviousSerb entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(PreviousSerb entity, DSRequest dsRequest) throws Exception {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try{
			PreviousSerb previousSerb = seafarerService.update(entity);
			dsResponse.setTotalRows(1);
			dsResponse.setData(previousSerb);
			return dsResponse;
			
		}catch(Exception ex){
			logger.error("Fail to Update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
		
	}
	
	@Override
	public DSResponse add(PreviousSerb entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			PreviousSerb dbPreviousSerb = seafarerService.reIssueSerb(entity);
			dsResponse.setTotalRows(1);
			dsResponse.setData(dbPreviousSerb);
			return dsResponse;
		} catch (Exception ex){
			logger.error("Fail to renew-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}
	
	@Override
	public DSResponse remove(PreviousSerb entity, DSRequest dsRequest) throws Exception {
		return super.remove(entity, dsRequest);
	}
	
}
