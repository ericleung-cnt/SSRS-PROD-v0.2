package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.user.FuncEntitle;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class FuncEntitlDMI extends AbstractCodeTableDMI<FuncEntitle> {

	@Override
	public DSResponse fetch(FuncEntitle entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(FuncEntitle entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(FuncEntitle entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
