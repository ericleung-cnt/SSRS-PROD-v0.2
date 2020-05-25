package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.ReasonCode;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ReasonCodeDMI extends AbstractCodeTableDMI<ReasonCode> {

	@Override
	public DSResponse fetch(ReasonCode entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(ReasonCode entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(ReasonCode entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
