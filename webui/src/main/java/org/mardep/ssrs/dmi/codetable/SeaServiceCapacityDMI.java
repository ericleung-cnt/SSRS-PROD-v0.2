package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Rank;
import org.mardep.ssrs.domain.codetable.SeaServiceCapacity;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SeaServiceCapacityDMI extends AbstractCodeTableDMI<SeaServiceCapacity> {

	@Override
	public DSResponse fetch(SeaServiceCapacity entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(SeaServiceCapacity entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(SeaServiceCapacity entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
