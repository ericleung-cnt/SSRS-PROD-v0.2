package org.mardep.ssrs.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Leo.LIANG
 *
 */
@Entity
@Table(name="USER_ROLES")
@IdClass(UserRolePK.class)
@NoArgsConstructor
@ToString(of={"userId","roleId"})
public class UserRole extends AbstractPersistentEntity<UserRolePK>{

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "USER_ID", nullable = false, length=10)
	private String userId;

	@Id
	@Getter
	@Setter
	@Column(name = "ROLE_ID", nullable = false)
	private Long roleId;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="USER_ID", referencedColumnName="USER_ID", updatable=false, insertable=false)
	private User user;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_ID", referencedColumnName="ROLE_ID", updatable=false, insertable=false)
	private Role role;
	
	@Override
	public UserRolePK getId() {
		return new UserRolePK(userId, roleId);
	}

}
 