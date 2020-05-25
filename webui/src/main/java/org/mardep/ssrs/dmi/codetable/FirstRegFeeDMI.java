package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.FirstRegFee;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class FirstRegFeeDMI extends AbstractCodeTableDMI<FirstRegFee> {

	@Override
	public DSResponse fetch(FirstRegFee entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(FirstRegFee entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(FirstRegFee entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
