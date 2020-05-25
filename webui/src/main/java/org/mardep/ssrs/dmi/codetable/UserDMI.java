package org.mardep.ssrs.dmi.codetable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mardep.ssrs.constant.ChangePasswordResult;
import org.mardep.ssrs.domain.codetable.Office;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.service.IUserService;
import org.mardep.ssrs.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class UserDMI extends AbstractCodeTableDMI<User> {

	@Autowired
	IUserService userService;
	
	@Override
	public DSResponse fetch(User entity, DSRequest dsRequest){
		if ("FIND_USER_OFFICE".equals(dsRequest.getOperationId())) {
			Map criteria = dsRequest.getCriteria();
			String id=(String)criteria.get("id");
			int result=userService.findUserOfficeIdByUserId(id);
			//User result = userService.findUserByUserId(id);
			DSResponse dsResponse = new DSResponse();
			dsResponse.setData(result);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;
			
		} else {
			return super.fetch(entity, dsRequest);
		}
	}

	@Override
	public DSResponse update(User entity, DSRequest dsRequest) throws Exception {
		if(StringUtils.isNotBlank(entity.getNewUserPassword())){
			String encryptPassword = PasswordUtil.encrypt(entity.getNewUserPassword());
			entity.setUserPassword(encryptPassword);
		}
		
		if("CHANGE_USER_PASSWORD".equals(dsRequest.getOperationId())){
			DSResponse dsResponse = new DSResponse();
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			logger.info("#change User Password");
			@SuppressWarnings("unchecked")
			Map<String, Object> values = dsRequest.getValues();
			String userId = values.get("userId").toString();
			String oldPassword = values.get("oldPassword").toString();
			String newPassword = values.get("newPassword").toString();
			String newPasswordConfirm = values.get("newPasswordConfirm").toString();
			try {
				ChangePasswordResult result = userService.changePassword(userId, oldPassword, newPassword, newPasswordConfirm);
				dsResponse.setData(result.getMessage());
			} catch (Exception ex){
				logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);
				dsResponse = handleException(dsResponse, ex);
			}
			return dsResponse;
		}else if (entity.getCreatedBy() == null) {
			Set<Long> roles = entity.getRoleIds();
			entity.setRoleIds(new HashSet<>());
			DSResponse response = super.update(entity, dsRequest);
			if (response.getStatus() == DSResponse.STATUS_SUCCESS) {
				entity = (User) response.getData();
				entity.setRoleIds(roles);
				return super.update(entity, dsRequest);
			} else {
				return response;
			}
		}
		return super.update(entity, dsRequest);
	}

	@Override
	public DSResponse add(User entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}

}
