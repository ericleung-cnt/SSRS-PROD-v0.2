package org.mardep.ssrs.dao.user;

import java.util.Date;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.user.LogStatus;
import org.mardep.ssrs.domain.user.UserAccessLog;
import org.mardep.ssrs.domain.user.UserAccessLogPK;
import org.springframework.stereotype.Repository;

@Repository
public class UserAccessLogJpaDao extends AbstractJpaDao<UserAccessLog, UserAccessLogPK> implements IUserAccessLogDao {

	@Override
	public UserAccessLog signOn(String userId, Date signOnTime, LogStatus status, String ipAddress) {
		// TODO Auto-generated method stub
		logger.debug("#signOn:{}, Time:{}, Status:{}", new Object[]{userId, signOnTime, status});
		try{
			UserAccessLog entity = new UserAccessLog(userId, signOnTime);
			entity.setStatus(status);
			entity.setIpAddress(ipAddress);
			UserAccessLog dbEntity = em.merge(entity);
			return dbEntity;
		} catch (Exception ex){
			//throw new SecsDaoException("fail to create UserAccessLog on #signOn", ex);
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public Boolean signOff(String userId, Date signOnTime, Date signOffTime, String ipAddress) {
		// TODO Auto-generated method stub
		logger.debug("#signOff:{}, signOnTime:{}, signOffTime:{}", new Object[]{userId, signOnTime, signOffTime});
		try{
			StringBuffer sb = new StringBuffer();
			sb.append("update UserAccessLog set signOffTime=:signOffTime, ipAddress=:ipAddress ");
			sb.append("where userId =:userId ");
			sb.append("and signOnTime =:signOnTime");
			Query q = em.createQuery(sb.toString());
			q.setParameter("signOffTime", signOffTime);
			q.setParameter("ipAddress", ipAddress);
			q.setParameter("userId", userId);
			q.setParameter("signOnTime", signOnTime);
			Integer i = q.executeUpdate();
			return i==1?Boolean.TRUE:Boolean.FALSE;
		} catch (Exception ex){
			//throw new SecsDaoException("fail to create UserAccessLog on #signOff", ex);
			ex.printStackTrace();
			return Boolean.FALSE;
		}
	}

}
