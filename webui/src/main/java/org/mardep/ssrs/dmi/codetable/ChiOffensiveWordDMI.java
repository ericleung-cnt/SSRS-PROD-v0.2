package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.ChiOffensiveWord;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ChiOffensiveWordDMI extends AbstractCodeTableDMI<ChiOffensiveWord> {

	@Override
	public DSResponse fetch(ChiOffensiveWord entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(ChiOffensiveWord entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(ChiOffensiveWord entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
