package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Authority;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class AuthorityDMI extends AbstractCodeTableDMI<Authority> {

	@Override
	public DSResponse fetch(Authority entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Authority entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Authority entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
