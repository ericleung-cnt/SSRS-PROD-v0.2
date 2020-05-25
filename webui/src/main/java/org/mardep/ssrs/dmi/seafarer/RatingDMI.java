package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.Rating;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class RatingDMI extends AbstractSeafarerDMI<Rating> {

	@Override
	public DSResponse fetch(Rating entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Rating entity, DSRequest dsRequest) throws Exception {
		entity.setSeafarer(null);
		entity.setRank(null);
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Rating entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
	@Override
	public DSResponse remove(Rating entity, DSRequest dsRequest) throws Exception {
		return super.remove(entity, dsRequest);
	}
	
}
