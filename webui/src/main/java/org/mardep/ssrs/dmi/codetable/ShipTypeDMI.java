package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.ShipType;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ShipTypeDMI extends AbstractCodeTableDMI<ShipType> {

	@Override
	public DSResponse fetch(ShipType entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(ShipType entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(ShipType entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
