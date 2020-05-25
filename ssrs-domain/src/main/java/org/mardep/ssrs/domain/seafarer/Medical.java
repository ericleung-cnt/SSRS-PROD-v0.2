package org.mardep.ssrs.domain.seafarer;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.Clinic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="SEAFARER_MEDICAL")
@IdClass(CommonPK.class)
@AuditOverride(forClass=AbstractPersistentEntity.class)
@DynamicUpdate
@ToString(of={"seafarerId", "seqNo"})
public class Medical extends AbstractPersistentEntity<CommonPK> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@Column(name = "SEQ_NO", nullable=false, length=5)
	private Integer seqNo;

	@Id
	@Getter
	@Setter
	@Column(name = "SEAFARER_ID", nullable=false, length=20)
	private String seafarerId;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SF_SFM_FK"))
	private Seafarer seafarer;

	@Getter
	@Setter
	@Column(name = "MEDICAL_EXPIRY")
	@Temporal(TemporalType.DATE)
	private Date expiryDate;
	
	@Getter
	@Setter
	@Column(name = "MEDICAL_REMARK", length=60)
	private String medicalRemark;
	
	@Getter
	@Setter
	@Column(name = "DOCTOR_REMARK", length=60)
	private String doctorRemark;
	
	@Getter
	@Setter
	@Column(name = "XRAY_REMARK", length=60)
	private String xrayRemark;
	
	@Getter
	@Setter
	@Column(name = "CLINIC_NO")
	private Long clinicNo;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CLINIC_NO", referencedColumnName="CLINIC_SEQ_NO", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SM_SMC_FK"))
	private Clinic clinic;
	
	@Getter
	@Setter
	@Column(name = "EXAM_PLACE", length=30)
	private String examPlace; 

	@Getter
	@Setter
	@Column(name = "EXAM_DATE")
	@Temporal(TemporalType.DATE)
	private Date examDate; 

	@Override
	public CommonPK getId() {
		return new CommonPK(getSeqNo(), getSeafarerId());
	}

}
