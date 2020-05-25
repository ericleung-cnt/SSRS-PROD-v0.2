package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.EngOffensiveWord;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class EngOffensiveWordDMI extends AbstractCodeTableDMI<EngOffensiveWord> {

	@Override
	public DSResponse fetch(EngOffensiveWord entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(EngOffensiveWord entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(EngOffensiveWord entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
