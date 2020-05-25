package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.user.Role;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class RoleDMI extends AbstractCodeTableDMI<Role> {

	@Override
	public DSResponse fetch(Role entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Role entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Role entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
