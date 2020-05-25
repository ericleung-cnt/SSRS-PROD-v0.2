package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Agent;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class AgentDMI extends AbstractCodeTableDMI<Agent> {

	@Override
	public DSResponse fetch(Agent entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Agent entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Agent entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
