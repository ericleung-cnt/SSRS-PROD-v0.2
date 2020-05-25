package org.mardep.ssrs.dao.user;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.user.FuncEntitle;
import org.mardep.ssrs.domain.user.FuncEntitlePK;
import org.mardep.ssrs.domain.user.SystemFunc;

public interface IFuncEntitleDao extends IBaseDao<FuncEntitle, FuncEntitlePK> {

	List<SystemFunc> findSystemFuncByUserId(String userId);
	List<FuncEntitle> findByRoleId(Long roleId);
	List<SystemFunc> findSystemFuncByRoleList(List<String> roleList);
}
