package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.NextOfKin;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class NextOfKinDMI extends AbstractSeafarerDMI<NextOfKin> {

	@Override
	public DSResponse fetch(NextOfKin entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(NextOfKin entity, DSRequest dsRequest) throws Exception {
		entity.setSeafarer(null);
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(NextOfKin entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}

	@Override
	public DSResponse remove(NextOfKin entity, DSRequest dsRequest) throws Exception {
		return super.remove(entity, dsRequest);
	}
	
}
