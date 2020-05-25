package org.mardep.ssrs.domain.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Deprecated
//@Entity
@Table(name="ROLE_TASK")
@NoArgsConstructor
public class RoleTask extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@Column(name = "ROLE_TASK_ID", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "ROLE_CODE", nullable=false, length=20)
	private String roleCode;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_CODE", referencedColumnName="ROLE_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="RF_ROLE_FK"))
	private Role role;

	@Getter
	@Setter
	@Column(name = "TASK_TYPE", nullable=false, length=50)
	private String taskType;

	@Getter
	@Setter
	@Column(name = "TASK_DESC", nullable=false, length=1000) // TODO length
	private String taskDesc;

	@Getter
	@Setter
	@Column(name = "PICKUP_BY", nullable=false, length=50)
	private String pickupBy;

	@Column(name = "PICKUP_DATE", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date pickupDate;

	@Override
	public Long getId() {
		return id;
	}

}
