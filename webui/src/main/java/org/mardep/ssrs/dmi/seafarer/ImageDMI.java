package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.Image;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ImageDMI extends AbstractSeafarerDMI<Image> {

	@Override
	public DSResponse fetch(Image entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Image entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Image entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
