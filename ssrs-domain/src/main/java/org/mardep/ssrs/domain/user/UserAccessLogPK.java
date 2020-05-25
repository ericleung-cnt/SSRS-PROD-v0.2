package org.mardep.ssrs.domain.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Embeddable
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = { "userId", "signOnTime" })
@ToString()
@Getter
public class UserAccessLogPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private String userId;
	
	@NonNull
	private Date signOnTime;

//	public UserAccessLogPK(String userId, Date signOnTime) {
//		super();
//		this.userId = userId;
//		this.signOnTime = signOnTime;
//	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((signOnTime == null) ? 0 : signOnTime.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserAccessLogPK other = (UserAccessLogPK) obj;
		if (signOnTime == null) {
			if (other.signOnTime != null)
				return false;
		} else if (!signOnTime.equals(other.signOnTime))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
}
