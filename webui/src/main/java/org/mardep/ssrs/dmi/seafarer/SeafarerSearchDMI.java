package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.SeafarerSearch;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SeafarerSearchDMI extends AbstractSeafarerDMI<SeafarerSearch> {

	@Override
	public DSResponse fetch(SeafarerSearch entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

}
