package org.mardep.ssrs.dao.user;

import java.util.Date;

import org.mardep.ssrs.domain.user.LogStatus;
import org.mardep.ssrs.domain.user.UserAccessLog;

public interface IUserAccessLogDao {

	public UserAccessLog signOn(String userId, Date signOnTime, LogStatus status, String ipAddress);
	public Boolean signOff(String userId, Date signOnTime, Date signOffTime, String ipAddress);

}
