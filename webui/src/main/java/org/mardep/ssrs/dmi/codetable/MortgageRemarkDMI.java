package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.MortgageRemark;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class MortgageRemarkDMI extends AbstractCodeTableDMI<MortgageRemark> {

	@Override
	public DSResponse fetch(MortgageRemark entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(MortgageRemark entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(MortgageRemark entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
