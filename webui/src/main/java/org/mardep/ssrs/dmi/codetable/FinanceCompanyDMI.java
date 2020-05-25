package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.FinanceCompany;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class FinanceCompanyDMI extends AbstractCodeTableDMI<FinanceCompany> {

	@Override
	public DSResponse fetch(FinanceCompany entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(FinanceCompany entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(FinanceCompany entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
