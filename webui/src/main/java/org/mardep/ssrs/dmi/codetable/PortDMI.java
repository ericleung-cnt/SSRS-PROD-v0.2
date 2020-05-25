package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Port;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class PortDMI extends AbstractCodeTableDMI<Port> {

	@Override
	public DSResponse fetch(Port entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Port entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Port entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
