package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.MmoShipType;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class MmoShipTypeDMI extends AbstractCodeTableDMI<MmoShipType> {

	@Override
	public DSResponse fetch(MmoShipType entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(MmoShipType entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(MmoShipType entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
