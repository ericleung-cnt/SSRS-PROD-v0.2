package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Registrar;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class RegistrarDMI extends AbstractCodeTableDMI<Registrar> {

	@Override
	public DSResponse fetch(Registrar entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Registrar entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Registrar entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
