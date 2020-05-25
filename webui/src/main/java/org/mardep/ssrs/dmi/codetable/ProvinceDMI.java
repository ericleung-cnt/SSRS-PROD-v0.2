package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Province;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ProvinceDMI extends AbstractCodeTableDMI<Province> {

	@Override
	public DSResponse fetch(Province entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Province entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Province entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
