package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Vessel;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class VesselDMI extends AbstractCodeTableDMI<Vessel> {

	@Override
	public DSResponse fetch(Vessel entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Vessel entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Vessel entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
