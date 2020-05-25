package org.mardep.ssrs.domain.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Deprecated
@Entity
@Table(name="SSRS_LDAP_USERS")
@NoArgsConstructor
public class LdapUser extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Column(name = "SSRS_USER_ID", nullable=false, length=30)
	private String id;

	@Getter
	@Setter
	@Column(name = "LDAP_USER_ID", length=30)
	private String ldapUserId;
	
	@Column(name = "CREATION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	protected Date creationDate;

	@Override
	public String getId() {
		return id;
	}

}
