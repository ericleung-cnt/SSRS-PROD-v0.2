package org.mardep.ssrs.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class AbstractPersistentEntity<T extends Serializable> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Transient
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Column(name = "CREATE_DATE", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	protected Date createdDate;
	
	@Column(name = "LASTUPD_DATE", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	protected Date updatedDate;
	
	@Column(name = "CREATE_BY", nullable=false, length=50)
	@Getter
	@Setter
	protected String createdBy;
	
	@Column(name = "LASTUPD_BY", nullable=false, length=50)
	@Getter
	@Setter
	protected String updatedBy;
	
	@Version
	@Column(name = "ROWVERSION")
	@Getter
	@Setter
	protected Long version;
	
	public abstract T getId();
	
	@PreUpdate
	public void onPreUpdate() {
		Date currentDate = new Date();
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		logger.debug("#onPreUpdate - CurrentUser:{}", currentUser);
		setUpdatedBy(currentUser);
		setUpdatedDate(currentDate);
	}
	
	@PrePersist
	public void onPreInsert() {
		Date currentDate = new Date();
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		logger.debug("onPreInsert - CurrentUser:{}", currentUser);
		setUpdatedDate(currentDate);
		setCreatedDate(currentDate);
		setUpdatedBy(currentUser);
		setCreatedBy(currentUser);
	}
	
}
