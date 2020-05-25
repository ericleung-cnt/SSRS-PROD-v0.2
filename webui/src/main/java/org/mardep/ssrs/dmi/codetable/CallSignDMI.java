package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.CallSign;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CallSignDMI extends AbstractCodeTableDMI<CallSign> {

	@Override
	public DSResponse fetch(CallSign entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(CallSign entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(CallSign entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
