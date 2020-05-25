package org.mardep.ssrs.dao.user;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.user.UserRole;
import org.mardep.ssrs.domain.user.UserRolePK;

public interface IUserRoleDao extends IBaseDao<UserRole, UserRolePK> {

	List<UserRole> findUserRoleByUserId(String userId);
}
