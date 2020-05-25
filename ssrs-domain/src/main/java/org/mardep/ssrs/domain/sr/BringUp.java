package org.mardep.ssrs.domain.sr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
//@Entity
@Table(name="BRING_UPS")
@IdClass(BringUpPK.class)
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "bringUpDate"})
public class BringUp extends AbstractPersistentEntity<BringUpPK> {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	@Getter
	@Setter
	@Column(name = "RM_APPL_NO_SUF", length=1)
	private String applNoSuf;

	@Id
	@Getter
	@Setter
	@Column(name = "BRING_UP_DATE")
	@Temporal(TemporalType.DATE)
	private Date bringUpDate;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false),
		@JoinColumn(name="RM_APPL_NO_SUF", referencedColumnName="APPL_NO_SUF", updatable=false, insertable=false)
    }, foreignKey=@ForeignKey(name="BU_RMR_FK")
	)
	private RegMaster regMaster; // TODO no FK on db def.

	@Getter
	@Setter
	@Column(name = "BRING_UP_ACTION", length=40)
	private String bringUpAction;

	@Override
	public BringUpPK getId() {
		return new BringUpPK(getApplNo(), getBringUpDate());
	}

}
