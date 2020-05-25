package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.ShipSubType;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ShipSubTypeDMI extends AbstractCodeTableDMI<ShipSubType> {

	@Override
	public DSResponse fetch(ShipSubType entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(ShipSubType entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(ShipSubType entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
