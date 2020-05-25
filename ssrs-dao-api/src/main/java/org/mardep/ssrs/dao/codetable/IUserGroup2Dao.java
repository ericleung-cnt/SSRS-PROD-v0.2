package org.mardep.ssrs.dao.codetable;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.codetable.UserGroup2;

public interface IUserGroup2Dao extends IBaseDao<UserGroup2, Long> {
	List<UserGroup2> getAll();
}

