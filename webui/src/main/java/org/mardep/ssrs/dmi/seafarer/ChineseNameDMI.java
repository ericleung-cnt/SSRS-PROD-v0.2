package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.ChineseName;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ChineseNameDMI extends AbstractSeafarerDMI<ChineseName> {

	@Override
	public DSResponse fetch(ChineseName entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(ChineseName entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(ChineseName entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
