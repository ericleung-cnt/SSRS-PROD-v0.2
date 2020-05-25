package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Lawyer;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class LawyerDMI extends AbstractCodeTableDMI<Lawyer> {

	@Override
	public DSResponse fetch(Lawyer entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Lawyer entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Lawyer entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
