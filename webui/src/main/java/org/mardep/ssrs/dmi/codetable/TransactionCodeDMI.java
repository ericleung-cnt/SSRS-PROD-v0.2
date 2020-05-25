package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.TransactionCode;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class TransactionCodeDMI extends AbstractCodeTableDMI<TransactionCode> {

	@Override
	public DSResponse fetch(TransactionCode entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(TransactionCode entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(TransactionCode entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
