package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.OperationType;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class OperationTypeDMI extends AbstractCodeTableDMI<OperationType> {

	@Override
	public DSResponse fetch(OperationType entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(OperationType entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(OperationType entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
