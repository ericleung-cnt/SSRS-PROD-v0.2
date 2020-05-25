package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.user.UserRole;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class UserRoleDMI extends AbstractCodeTableDMI<UserRole> {

	@Override
	public DSResponse fetch(UserRole entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(UserRole entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(UserRole entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
