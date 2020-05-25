package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.StopList;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class StopListDMI extends AbstractCodeTableDMI<StopList> {

	@Override
	public DSResponse fetch(StopList entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(StopList entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(StopList entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
