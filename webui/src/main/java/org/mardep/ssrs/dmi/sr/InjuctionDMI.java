package org.mardep.ssrs.dmi.sr;

import org.mardep.ssrs.domain.sr.Injuction;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class InjuctionDMI extends AbstractSrDMI<Injuction> {

	@Override
	public DSResponse fetch(Injuction entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Injuction entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}

	@Override
	public DSResponse add(Injuction entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}


}
