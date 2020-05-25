package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.Cert;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CertDMI extends AbstractSeafarerDMI<Cert> {

	@Override
	public DSResponse fetch(Cert entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Cert entity, DSRequest dsRequest) throws Exception {
		entity.setSeafarer(null);
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Cert entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
	@Override
	public DSResponse remove(Cert entity, DSRequest dsRequest) throws Exception {
		return super.remove(entity, dsRequest);
	}
	
}
