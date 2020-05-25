package org.mardep.ssrs.domain.sr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.hibernate.envers.Audited;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="MORTGAGES")
@IdClass(MortgagePK.class)
@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "priorityCode"})
public class Mortgage extends AbstractPersistentEntity<MortgagePK> {

	private static final long serialVersionUID = 1L;

	public static final String STATUS_RECEIVED = "R";

	public static final String STATUS_PENDING = "P";

	public static final String STATUS_APPROVED = "a";

	public static final String STATUS_ACTIVE = "A";

	public static final String STATUS_WITHDRAW = "W";

	public static final String STATUS_DISCHARGED = "D";

	public static final String STATUS_CANCELLED = "C";

	@Id
	@Getter
	@Setter
	@Column(name = "MOR_RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	@Id
	@Getter
	@Setter
	@Column(name = "MOR_PRIORITY_CODE", nullable=false, length=2)
	private String priorityCode;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="MOR_RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false),
    }, foreignKey=@ForeignKey(name="MOR_RM_FK")
	)
	private RegMaster regMaster;


	@Getter
	@Setter
	@Column(name = "AGREE_TXT", length=2000)
	private String agreeTxt; // TODO 2000??

	@Getter
	@Setter
	@Column(name = "HIGHER_MORTGAGEE_CONSENT", length=1)
	private String higherMortgageeConsent;

	@Getter
	@Setter
	@Column(name = "CONSENT_CLOSURE", length=1)
	private String consentClosure;

	@Getter
	@Setter
	@Column(name = "CONSENT_TRANSFER", length=1)
	private String consentTransfer;

	@Getter
	@Setter
	@Column(name = "MORT_STATUS", length=1)
	private String mortStatus; // TODO enum

	@Getter
	@Setter
	@Column(name = "REG_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date regTime;

	@Override
	public MortgagePK getId() {
		return new MortgagePK(getApplNo(), getPriorityCode());
	}

}
