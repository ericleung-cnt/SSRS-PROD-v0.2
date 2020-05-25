package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.ShipList;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ShipListDMI extends AbstractCodeTableDMI<ShipList> {

	@Override
	public DSResponse fetch(ShipList entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(ShipList entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(ShipList entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
