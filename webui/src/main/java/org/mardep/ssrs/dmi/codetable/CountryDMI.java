package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Country;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CountryDMI extends AbstractCodeTableDMI<Country> {

	@Override
	public DSResponse fetch(Country entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Country entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Country entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
