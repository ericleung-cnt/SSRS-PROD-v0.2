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
@Table(name="STAFF_ROLE")
@NoArgsConstructor
@IdClass(StaffRolePK.class)
@ToString(of={"staffId","roleCode"})
public class StaffRole extends AbstractPersistentEntity<StaffRolePK> {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "STAFF_ID", nullable=false, length=80)
	private String staffId;

	@Id
	@Getter
	@Setter
	@Column(name = "ROLE_CODE", nullable=false, length=20)
	private String roleCode;


	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_CODE", referencedColumnName="ROLE_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SR_ROLE_FK"))
	private Role role;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="STAFF_ID", referencedColumnName="STAFF_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SR_STAFF_FK"))
	private Staff staff;

	@Override
	public StaffRolePK getId() {
		return new StaffRolePK(getStaffId(), getRoleCode());
	}

}
