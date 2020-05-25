package org.mardep.ssrs.domain.user;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
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


@Deprecated
//@Entity
@Table(name="ROLE_FUNC")
@IdClass(RoleFuncPK.class)
@NoArgsConstructor
@ToString(of={"roleCode", "funcId"})
public class RoleFunc extends AbstractPersistentEntity<RoleFuncPK> {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "ROLE_CODE", nullable=false, length=20)
	private String roleCode;

	@Id
	@Getter
	@Setter
	@Column(name = "FUNC_ID", nullable=false, length=10)
	private String funcId;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_CODE", referencedColumnName="ROLE_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="RF_ROLE_FK"))
	private Role role;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FUNC_ID", referencedColumnName="FUNC_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="RF_FUNC_FK"))
	private Func func;

	@Override
	public RoleFuncPK getId() {
		return new RoleFuncPK(getRoleCode(), getFuncId());
	}

}
