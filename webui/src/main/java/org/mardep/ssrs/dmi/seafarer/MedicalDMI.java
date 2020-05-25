package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.Medical;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class MedicalDMI extends AbstractSeafarerDMI<Medical> {

	@Override
	public DSResponse fetch(Medical entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Medical entity, DSRequest dsRequest) throws Exception {
		entity.setSeafarer(null);
		entity.setClinic(null);
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Medical entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
	@Override
	public DSResponse remove(Medical entity, DSRequest dsRequest) throws Exception {
		return super.remove(entity, dsRequest);
	}
	
}
