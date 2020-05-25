package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Nationality;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class NationalityDMI extends AbstractCodeTableDMI<Nationality> {

	@Override
	public DSResponse fetch(Nationality entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Nationality entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Nationality entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
