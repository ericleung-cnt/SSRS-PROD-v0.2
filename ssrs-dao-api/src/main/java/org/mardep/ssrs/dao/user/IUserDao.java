package org.mardep.ssrs.dao.user;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.user.User;

public interface IUserDao extends IBaseDao<User, String> {
	String findOfficeCodeByUserID(String id);
}
