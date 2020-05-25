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

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="SEAFARER_EMPLOYMENT")
@IdClass(CommonPK.class)
//@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"seafarerId", "seqNo"})
public class Employment extends AbstractPersistentEntity<CommonPK> {

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
	@JoinColumn(name="SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SF_SFE_FK"))
	private Seafarer seafarer;

	@Getter
	@Setter
	@Column(name = "COMPANY_NAME", length=50)
	private String companyName; // TODO no FK
	
//	@Getter
//	@Setter
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="COMPANY_NAME", referencedColumnName="PERMITTED_COMPANY_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SE_CN_FK"))
//	private PermittedCompany permittedcompany;


	@Getter
	@Setter
	@Column(name = "LISTING_DATE")
	@Temporal(TemporalType.DATE)
	private Date listingDate; 

	@Getter
	@Setter
	@Column(name = "CANCEL_DATE")
	@Temporal(TemporalType.DATE)
	private Date cancelDate; 

	@Override
	public CommonPK getId() {
		return new CommonPK(getSeqNo(), getSeafarerId());
	}

}
