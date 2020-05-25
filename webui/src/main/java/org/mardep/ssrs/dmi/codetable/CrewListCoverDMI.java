package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.CrewListCover;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CrewListCoverDMI extends AbstractCodeTableDMI<CrewListCover> {

	@Override
	public DSResponse fetch(CrewListCover entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(CrewListCover entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(CrewListCover entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
