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
@Table(name="SEAFARER_CERT")
@IdClass(CommonPK.class)
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"seafarerId", "seqNo"})
public class Cert extends AbstractPersistentEntity<CommonPK> {

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
	@JoinColumn(name="SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SF_SFC_FK"))
	private Seafarer seafarer;

	@Getter
	@Setter
	@Column(name = "CERT_TYPE", length=120)
	private String certType; // TODO enum

	@Getter
	@Setter
	@Column(name = "ISSUE_DATE")
	@Temporal(TemporalType.DATE)
	private Date issueDate; 

	@Getter
	@Setter
	@Column(name = "ISSUE_AUTHORITY", length=40) 	
	private String issueAuthority; 
	
//	@Getter
//	@Setter
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="ISSUE_AUTHORITY", referencedColumnName="APPROVED_ISSUING_AUTHORITY_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="CERT_AUTHORITY_FK"))
//	private Authority authority;
//	NO FK here and length not match
	
	@Getter
	@Setter
	@Column(name = "CERT_NO", length=30)
	private String certNo; 
	
	@Override
	public CommonPK getId() {
		return new CommonPK(getSeqNo(), getSeafarerId());
	}

}
