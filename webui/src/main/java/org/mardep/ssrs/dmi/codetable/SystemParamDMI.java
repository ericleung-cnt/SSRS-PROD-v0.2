package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.SystemParam;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SystemParamDMI extends AbstractCodeTableDMI<SystemParam> {

	@Override
	public DSResponse fetch(SystemParam entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(SystemParam entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(SystemParam entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
