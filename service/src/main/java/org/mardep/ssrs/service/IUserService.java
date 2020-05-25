package org.mardep.ssrs.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.constant.ChangePasswordResult;
import org.mardep.ssrs.domain.codetable.Office;
import org.mardep.ssrs.domain.user.SystemFunc;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserRole;

/**
 * 
 * @author Leo.LIANG
 *
 */
public interface IUserService extends IBaseService{

	Map<String, Object> login(String userId, String password, boolean isExternal, String remoteAddress);
	void logout(String userId, Date signOnTime, Date signOffTime, String remoteAddress);

	ChangePasswordResult changePassword(String userId, String oldPassword, String newPassword, String newPasswordConfirm);
	
	List<UserRole> findUserRoleByUserId(String userId);
	List<SystemFunc> findSystemFuncByUserId(String userId);
	int findUserOfficeIdByUserId(String userId);
	User findUserByUserId(String userId);
}
