package org.mardep.ssrs.domain.sr;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.Registrar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="CSR_FORMS")
@IdClass(CsrFormPK.class)
//@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "imoNo", "formSeq"})
public class CsrForm extends AbstractPersistentEntity<CsrFormPK> {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "IMO_NO", nullable=false, length=9)
	private String imoNo;

	@Id
	@Getter
	@Setter
	@Column(name = "FORM_SEQ", nullable=false)
	private Integer formSeq;

	@Getter
	@Setter
	@Column(name = "APPL_NO", nullable=false, length=9)
	private String applNo;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false),
    }, foreignKey=@ForeignKey(name="CFM_RMR_FK")
	)
	private RegMaster regMaster;

	@Getter
	@Setter
	@Column(name = "FORM_APPLY_DATE")
	@Temporal(TemporalType.DATE)
	private Date formApplyDate;

	@Getter
	@Setter
	@Column(name = "FLAG_STATE", length=25)
	private String flagState;

	@Getter
	@Setter
	@Column(name = "REGISTRATION_DATE")
	@Temporal(TemporalType.DATE)
	private Date registrationDate;

	@Getter
	@Setter
	@Column(name = "SHIP_NAME", length=40, nullable=false)
	private String shipName;

	@Getter
	@Setter
	@Column(name = "CLASS_SOCIETY", length=50)
	private String classSocietyId;

	@Getter
	@Setter
	@Column(name = "AUTHORIZED_PERSON", length=40)
	private String registrarName;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="AUTHORIZED_PERSON", referencedColumnName="REG_NAME1", updatable=false, insertable=false, foreignKey=@ForeignKey(name="FK_MEDICAL_SEAFARER_ID"))
	private Registrar registrar;

	@Getter
	@Setter
	@Column(name = "SHIP_MANAGER", length=60)
	private String shipManager;

	@Getter
	@Setter
	@Column(name = "SHIP_MGR_ADDRESS1", length=100)
	private String shipManagerAddress1;

	@Getter
	@Setter
	@Column(name = "SHIP_MGR_ADDRESS2", length=100)
	private String shipManagerAddress2;

	@Getter
	@Setter
	@Column(name = "SHIP_MGR_ADDRESS3", length=100)
	private String shipManagerAddress3;

	@Getter
	@Setter
	@Column(name = "SAFETY_ACT_ADDRESS1", length=100)
	private String safetyActAddress1;

	@Getter
	@Setter
	@Column(name = "SAFETY_ACT_ADDRESS2", length=100)
	private String safetyActAddress2;

	@Getter
	@Setter
	@Column(name = "SAFETY_ACT_ADDRESS3", length=100)
	private String safetyActAddress3;

	@Getter
	@Setter
	@Column(name = "DOC_AUTHORITY", length=3)
	private String docAuthority;

	@Getter
	@Setter
	@Column(name = "DOC_AUDIT", length=3)
	private String docAudit;

	@Getter
	@Setter
	@Column(name = "SMC_AUTHORITY", length=3)
	private String smcAuthority;

	@Getter
	@Setter
	@Column(name = "SMC_AUDIT", length=3)
	private String smcAudit;

	@Getter
	@Setter
	@Column(name = "ISSC_AUTHORITY", length=3)
	private String isscAuthority;

	@Getter
	@Setter
	@Column(name = "ISSC_AUDIT", length=3)
	private String isscAudit;

	@Column(name = "DEREG_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date deregDate;

	@Column(name = "CSR_ISSUE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date csrIssueDate;

	@Getter
	@Setter
	@Column(name = "REMARK", length=2000)
	private String remark;

	@Getter
	@Setter
	@Column(name = "CLASS_SOCIETY_2", length=40)
	private String classSociety2;

	@Getter
	@Setter
	@Column(name = "CSR_REMARKS", length=100)
	private String csrRemarks;

	@Getter
	@Setter
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumns(value={
		@JoinColumn(name="IMO_NO",referencedColumnName="IMO_NO"),
		@JoinColumn(name="FORM_SEQ",referencedColumnName="FORM_SEQ")
	}, foreignKey=@ForeignKey(name="FK_CSR_FORM_OWNERS"))
	private List<CsrFormOwner> owners;

	@Override
	public CsrFormPK getId() {
		return new CsrFormPK(getImoNo(), getFormSeq());
	}

	@Getter
	@Setter
	@Column(name = "FORM_ACCEPTED_DATE")
	private Date formAccepted;

	@Getter
	@Setter
	@Column(name = "FSQC_CONFIRMED")
	private Date fsqcConfirmed;

	@Getter
	@Setter
	@Column(name = "PORTFOLIO_RECEIVED_DATE")
	private Date portfolioReceived;

	@Getter
	@Setter
	@Column(name = "SR_APPROVED")
	private Date srApproved;

	@Getter
	@Setter
	@Column(name = "CSR_ISSUED")
	private Date csrIssued;

	@Getter
	@Setter
	@Column(name = "REP_INFORM_DATE")
	private Date repInformed;

	@Getter
	@Setter
	@Column(name = "CSR_COLLECTED_DATE")
	private Date csrCollected;

	@Getter
	@Setter
	@Column(name = "SQA_UPDATE_REQUIRED")
	private Boolean sqaUpdateRequired;
	@Getter
	@Setter
	@Column(name = "COMPANY_COPY_RECD")
	private Boolean companyCopyReceived;
	@Getter
	@Setter
	@Column(name = "FORMER_FLAG_REQD")
	private Boolean formerFlagRequested;

	@Getter
	@Setter
	@Column(name = "FORMER_FLAG_REMINDED")
	private Boolean formerFlagReminded;
	@Getter
	@Setter
	@Column(name = "FORM2_REVISED_REQD")
	private Boolean revisedRequired;
	@Getter
	@Setter
	@Column(name="IMO_OWNER_ID", length=20)
	private String imoOwnerId;
	@Getter
	@Setter
	@Column(name="IMO_COMPANY_ID", length=20)
	private String imoCompanyId;

	@Getter
	@Setter
	@Column(name = "INPUT_DATE")
	@Temporal(TemporalType.DATE)
	private Date inputDate;

	@Getter
	@Setter
	@Column(name = "APPLICANT_NAME", length=80)
	private String applicantName;

	@Getter
	@Setter
	@Column(name = "APPLICANT_EMAIL", length=80)
	private String applicantEmail;

}
