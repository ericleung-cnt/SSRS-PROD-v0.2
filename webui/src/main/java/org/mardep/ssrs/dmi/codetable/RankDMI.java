package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Rank;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class RankDMI extends AbstractCodeTableDMI<Rank> {

	@Override
	public DSResponse fetch(Rank entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Rank entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Rank entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
