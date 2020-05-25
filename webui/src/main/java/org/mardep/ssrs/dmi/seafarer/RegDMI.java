package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.Reg;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class RegDMI extends AbstractSeafarerDMI<Reg> {

	@Override
	public DSResponse fetch(Reg entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Reg entity, DSRequest dsRequest) throws Exception {
		entity.setSeafarer(null);
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Reg entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			Reg newReg = seafarerService.renew(entity);
			dsResponse.setTotalRows(1);
			dsResponse.setData(newReg);
			return dsResponse;
		} catch (Exception ex){
			logger.error("Fail to renew-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
//		return super.add(entity, dsRequest);
	}
	
	@Override
	public DSResponse remove(Reg entity, DSRequest dsRequest) throws Exception {
		return super.remove(entity, dsRequest);
	}
	
}
