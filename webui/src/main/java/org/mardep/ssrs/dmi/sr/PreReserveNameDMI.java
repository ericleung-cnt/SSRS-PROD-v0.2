package org.mardep.ssrs.dmi.sr;

import org.mardep.ssrs.domain.sr.PreReservedName;
import org.mardep.ssrs.service.IReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class PreReserveNameDMI extends AbstractSrDMI<PreReservedName> {
	@Autowired private IReservationService rs;
	@Override
	public DSResponse fetch(PreReservedName entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(PreReservedName entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(PreReservedName entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
	@Override
	public DSResponse remove(PreReservedName entity, DSRequest dsRequest) {
		DSResponse response = new DSResponse();
		PreReservedName result = rs.withdraw(entity.getId());
		response.setData(result);
		response.setStatus(DSResponse.STATUS_SUCCESS);
		return response;
	}
	
}
