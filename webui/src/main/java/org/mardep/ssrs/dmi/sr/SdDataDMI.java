package org.mardep.ssrs.dmi.sr;

import org.mardep.ssrs.domain.sr.SdData;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SdDataDMI extends AbstractSrDMI<SdData> {

	@Override
	public DSResponse fetch(SdData entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(SdData entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}

	@Override
	public DSResponse add(SdData entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}


}
