package org.mardep.ssrs.dmi.sr;

import org.mardep.ssrs.domain.sr.OwnerEnquiry;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class OwnerEnquiryDMI extends AbstractSrDMI<OwnerEnquiry> {

	@Override
	public DSResponse fetch(OwnerEnquiry entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(OwnerEnquiry entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}

	@Override
	public DSResponse add(OwnerEnquiry entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
}
