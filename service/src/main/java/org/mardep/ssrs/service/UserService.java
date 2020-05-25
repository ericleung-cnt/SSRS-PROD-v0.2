package org.mardep.ssrs.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.mardep.ssrs.constant.ChangePasswordResult;
import org.mardep.ssrs.constant.Key;
import org.mardep.ssrs.constant.UserLoginResult;
import org.mardep.ssrs.dao.codetable.IOfficeDao;
import org.mardep.ssrs.dao.codetable.IUserGroup2Dao;
import org.mardep.ssrs.dao.user.IFuncEntitleDao;
import org.mardep.ssrs.dao.user.IUserAccessLogDao;
import org.mardep.ssrs.dao.user.IUserDao;
import org.mardep.ssrs.dao.user.IUserRoleDao;
import org.mardep.ssrs.domain.codetable.Office;
import org.mardep.ssrs.domain.user.LogStatus;
import org.mardep.ssrs.domain.user.SystemFunc;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.domain.user.UserLoginType;
import org.mardep.ssrs.domain.user.UserRole;
import org.mardep.ssrs.domain.user.UserStatus;
import org.mardep.ssrs.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Leo.LIANG
 *
 */
@Service
@Transactional
public class UserService extends AbstractService implements IUserService{

	@Autowired
	LdapAuthenticationService ldapAuthenticationService;

	@Autowired
	IUserDao userDao;

	@Autowired
	IUserAccessLogDao userAccessLogDao;

	@Autowired
	IUserRoleDao userRoleDao;

	@Autowired
	IFuncEntitleDao funcEntitleDao;
	
	@Autowired
	IUserGroup2Dao	userGroupDao;
	
	@Autowired
	IOfficeDao officeDao;
	

	@Override
	public Map<String, Object> login(String userId, String password, boolean isExternal, String remoteAddress) {
		logger.info("[{}] | isExternal-[{}] try to login.", new Object[]{userId, isExternal});
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserLoginResult loginResult = UserLoginResult.SUCCESSFUL;
		int status = 0;
		User user = userDao.findById(userId);
		if(user==null){
			logger.info("#login result:{}", UserLoginResult.USER_NOT_EXIST.getMessage());
			loginResult = UserLoginResult.USER_NOT_EXIST;
		}else{
			user.setRemoteAddress(remoteAddress);
			if(isExternal){
				String encryptEnteredPassword = PasswordUtil.encrypt(password);

				if(!UserStatus.ACTIVE.getId().equals(user.getUserStatus())){
					logger.info("#login result:{}", UserLoginResult.USER_DISABLED.getMessage());
					loginResult = UserLoginResult.USER_DISABLED;
					status = -3;
				} else if(!encryptEnteredPassword.equals(user.getUserPassword())){
					logger.info("#login result:{}", UserLoginResult.PASSWORD_INCORRECT.getMessage());
					loginResult = UserLoginResult.PASSWORD_INCORRECT;
					status = -2;
				} /*else if(vitalDocSystmUserList.contains(userId)){
					user.setAdUserId(vitalDocSystemUserId);
					user.setUserLoginType(UserLoginType.EXTERNAL);
				} else{
					logger.info("User [{}] is external and not in VitalDoc System User List, would login DMS with its UserId/Password of SECS.", userId);
					user.setUserLoginType(UserLoginType.DMS);
				}/

				if("secssystem".equalsIgnoreCase(userId)){
					user.setUserLoginType(UserLoginType.AD);;
					user.setAdUserId(userId);
					user.setAdPassword(password);
				}*/
				else {
					resultMap.put(Key.CURRENT_USER, user);
				}
			}else{
				int authenticationReturnCode = ldapAuthenticationService.authenticate(userId, password);
				logger.debug("#LDAP login result:{}", authenticationReturnCode);

				if (authenticationReturnCode == 0){
					user.setUserLoginType(UserLoginType.AD);
					resultMap.put(Key.CURRENT_USER, user);
				}else if (authenticationReturnCode == 19){
					loginResult = UserLoginResult.LDAP_CHANGE_PASSWORD;
					status =  -1;
				}else{
					loginResult = UserLoginResult.LDAP_AUTH_FAILED;
					status =  -1;
				}
			}
		}

		Date loginTime = new Date();

		resultMap.put(Key.CURRENT_USER, user);
		if(user.getUserGroupIds()!=null) {
		resultMap.put(Key.OFFICE_CODE,user.getUserGroup().getOffice().getCode());
		}
		resultMap.put(Key.LOGIN_RESULT, loginResult);
		resultMap.put(Key.STATUS, status);
		resultMap.put(Key.IS_EXTERNAL, isExternal);
		resultMap.put(Key.LOGIN_TIME, loginTime);
		logger.info("LoginResult:{}", loginResult);

//		System.out.println("current user: " + UserContextThreadLocalHolder.getCurrentUserId());
		UserContextThreadLocalHolder.setCurrentUser(user);
		if (user != null) {
			userAccessLogDao.signOn(userId, loginTime, status == 0?LogStatus.S:LogStatus.F, remoteAddress);
		}

		return resultMap;
	}

	@Override
	public ChangePasswordResult changePassword(String userId, String oldPassword, String newPassword, String newPasswordConfirm){
		logger.debug("#changePassword:{}", userId);
		if (newPassword.equals(newPasswordConfirm)){
			User user = userDao.findById(userId);
			String encryptOldPassword = PasswordUtil.encrypt(oldPassword);
			String encryptNewPassword = PasswordUtil.encrypt(newPassword);
			if(!encryptOldPassword.equals(user.getUserPassword())){
				logger.debug("#changePassword result:{}", ChangePasswordResult.PASSWORD_INCORRECT);
				return ChangePasswordResult.PASSWORD_INCORRECT;
			}
			user.setUserPassword(encryptNewPassword);
			userDao.save(user);
		} else {
			return ChangePasswordResult.NEWPASSWORD_NOT_MATCH;
		}
		return ChangePasswordResult.SUCCESSFUL;
	}

	@Override
	public List<UserRole> findUserRoleByUserId(String userId) {
		return userRoleDao.findUserRoleByUserId(userId);
	}

	@Override
	public List<SystemFunc> findSystemFuncByUserId(String userId) {
		return funcEntitleDao.findSystemFuncByUserId(userId);
	}
	
	@Override
	public int findUserOfficeIdByUserId(String userId) {
	//return	userDao.findById(userId).getUserGroup().getOffice().getCode();
		String userGroupId=userDao.findById(userId).getUserGroupIds();
		String officeId=userGroupDao.findById(Long.parseLong(userGroupId)).getOfficeCode();
		//String officeCode=officeDao.findById(Integer.parseInt(OfficeId)).getCode();
		//Office office = officeDao.findByOfficeId(Integer.parseInt(officeId)); 
		return Integer.parseInt(officeId);
	}

	@Override
	public User findUserByUserId(String userId) {
		User user = userDao.findById(userId);
		//int officeId = user.getUserGroup().getOffice().getId();
		return user;
	}
	
	@Override
	public void logout(String userId, Date signOnTime, Date signOffTime, String remoteAddress) {
		// TODO Auto-generated method stub
		try {
			userAccessLogDao.signOff(userId, signOnTime, signOffTime, remoteAddress);
		} catch (Exception ex) {
			//throw new SecsServiceException("Fail to logout",e);
			ex.printStackTrace();
		}
	}
}
