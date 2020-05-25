package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Certificate;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CertificateDMI extends AbstractCodeTableDMI<Certificate> {

	@Override
	public DSResponse fetch(Certificate entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Certificate entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Certificate entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
