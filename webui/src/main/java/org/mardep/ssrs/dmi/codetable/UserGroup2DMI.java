package org.mardep.ssrs.dmi.codetable;

import java.util.List;

import org.mardep.ssrs.dao.codetable.IUserGroup2Dao;
import org.mardep.ssrs.domain.codetable.UserGroup2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class UserGroup2DMI extends AbstractCodeTableDMI<UserGroup2> {

	@Autowired
	IUserGroup2Dao	userGroupDao;
	
	@Override
	public DSResponse fetch(UserGroup2 entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
		// List<UserGroup2> resultList = userGroupDao.getAll();
		// DSResponse dsResponse = new DSResponse();
		// dsResponse.setData(resultList);
		
		// dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		// return dsResponse;
	}

	@Override
	public DSResponse update(UserGroup2 entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(UserGroup2 entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
