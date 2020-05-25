package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.Disciplinary;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class DisciplinaryDMI extends AbstractSeafarerDMI<Disciplinary> {

	@Override
	public DSResponse fetch(Disciplinary entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Disciplinary entity, DSRequest dsRequest) throws Exception {
		entity.setSeafarer(null);
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Disciplinary entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
	@Override
	public DSResponse remove(Disciplinary entity, DSRequest dsRequest) throws Exception {
		return super.remove(entity, dsRequest);
	}
	
}
