package org.mardep.ssrs.domain.sr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.Setter;

public class ReservedShipName extends AbstractPersistentEntity<ReservedShipNamePK> {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	@Id
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO_SUF", length=1)
	private String applNoSuf;

	@Id
	@Getter
	@Setter
	@Column(name = "NAME", nullable=false, length=40)
	private String name;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false),
		@JoinColumn(name="RM_APPL_NO_SUF", referencedColumnName="APPL_NO_SUF", updatable=false, insertable=false)
    }, foreignKey=@ForeignKey(name="OWR_RMR_FK")
	)
	private RegMaster regMaster; // TODO NO PK here


	@Getter
	@Setter
	@Column(name = "CNAME", length=40)
	private String chiName;

	@Getter
	@Setter
	@Column(name = "RESV_TIME")
	@Temporal(TemporalType.DATE)
	private Date resvTime;

	@Getter
	@Setter
	@Column(name = "EXPIRY_DATE")
	@Temporal(TemporalType.DATE)
	private Date expiryDate;

	@Getter
	@Setter
	@Column(name = "CNAME_REL_TIME")
	@Temporal(TemporalType.DATE)
	private Date cnameRelTime;

	@Getter
	@Setter
	@Column(name = "RELEASE_REASON", length=1)
	private String releaseReason;

	@Getter
	@Setter
	@Column(name = "CRESV_TIME")
	@Temporal(TemporalType.DATE)
	private Date cresvTime;

	@Getter
	@Setter
	@Column(name = "RELEASE_TIME")
	@Temporal(TemporalType.DATE)
	private Date releaseTime;

	@Getter
	@Setter
	@Column(name = "APPLICANT", length=40)
	private String applicant;

	@Getter
	@Setter
	@Column(name = "APPLICANT_ADDR1", length=80)
	private String addr1;

	@Getter
	@Setter
	@Column(name = "APPLICANT_ADDR2", length=80)
	private String addr2;

	@Getter
	@Setter
	@Column(name = "APPLICANT_ADDR3", length=80)
	private String addr3;

	@Getter
	@Setter
	@Column(name = "APPLICANT_TEL", length=18)
	private String tel;

	@Getter
	@Setter
	@Column(name = "APPLICANT_EMAIL", length=50)
	private String email;

	@Getter
	@Setter
	@Column(name = "CNAME_EXPIRY_TIME")
	@Temporal(TemporalType.DATE)
	private Date cnameExpiryTime;



	@Override
	public ReservedShipNamePK getId() {
		return new ReservedShipNamePK(getApplNo(), getApplNoSuf(), getName());
	}

}
