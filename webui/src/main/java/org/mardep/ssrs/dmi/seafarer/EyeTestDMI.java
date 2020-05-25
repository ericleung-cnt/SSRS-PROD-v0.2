package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.EyeTest;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class EyeTestDMI extends AbstractSeafarerDMI<EyeTest> {

	@Override
	public DSResponse fetch(EyeTest entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(EyeTest entity, DSRequest dsRequest) throws Exception {
		entity.setSeafarer(null);
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(EyeTest entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
