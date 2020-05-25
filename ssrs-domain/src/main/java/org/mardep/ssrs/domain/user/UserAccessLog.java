package org.mardep.ssrs.domain.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

@Entity
@Table(name="USER_ACCESS_LOGS")
@IdClass(UserAccessLogPK.class)
public class UserAccessLog extends AbstractPersistentEntity<UserAccessLogPK>{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "USER_ID", nullable = false, length=10)
	private String userId;
	
	@Id
	@Column(name = "SIGN_ON_TIME", nullable = false)  
	private Date signOnTime;

	@Column(name = "STATUS", length=1, nullable = false) 
	@Enumerated(EnumType.STRING)
	private LogStatus status;
	
	@Column(name = "SIGN_OFF_TIME")  
	private Date signOffTime;

	@Column(name = "IP_ADDRESS", length=50)  
	private String ipAddress;

	public UserAccessLog(){
		
	}

	public UserAccessLog(String userId, Date signOnTime) {
		super();
		this.userId = userId;
		this.signOnTime = signOnTime;
	}
	public UserAccessLog(UserAccessLogPK key) {
		this(key.getUserId(), key.getSignOnTime());
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getSignOnTime() {
		return signOnTime;
	}

	public void setSignOnTime(Date signOnTime) {
		this.signOnTime = signOnTime;
	}

	public LogStatus getStatus() {
		return status;
	}

	public void setStatus(LogStatus status) {
		this.status = status;
	}

	public Date getSignOffTime() {
		return signOffTime;
	}

	public void setSignOffTime(Date signOffTime) {
		this.signOffTime = signOffTime;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public UserAccessLogPK getId() {
		// TODO Auto-generated method stub
		return new UserAccessLogPK(userId, signOnTime);
	}

}
