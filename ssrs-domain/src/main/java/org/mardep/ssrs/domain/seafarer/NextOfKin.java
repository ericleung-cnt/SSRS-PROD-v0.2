package org.mardep.ssrs.domain.seafarer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="NEXT_OF_KIN")
@IdClass(CommonPK.class)
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"seafarerId", "seqNo"})
public class NextOfKin extends AbstractPersistentEntity<CommonPK> {

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
	@JoinColumn(name="SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="FK_NOK_SF"))
	private Seafarer seafarer;

	@Getter
	@Setter
	@Column(name = "KIN_NAME", length=40)
	private String kinName;

	@Getter
	@Setter
	@Column(name = "KIN_CHI_NAME", length=30)
	private String kinChiName;

	@Getter
	@Setter
	@Column(name = "KIN_HKID", length=15)
	private String kinHkid;

	@Getter
	@Setter
	@Column(name = "RELATION", length=20)
	private String relation;

	@Getter
	@Setter
	@Column(name = "MARRIAGE_CERT_NO", length=20)
	private String marriageCertNo;

	@Getter
	@Setter
	@Column(name = "ADDRESS1", length=240)
	private String addr1;

	@Getter
	@Setter
	@Column(name = "ADDRESS2", length=240)
	private String addr2;

	@Getter
	@Setter
	@Column(name = "ADDRESS3", length=240)
	private String addr3;

	@Getter
	@Setter
	@Column(name = "TELEPHONE", length=15)
	private String telephone;

	@Getter
	@Setter
	@Column(name = "STATUS", length=1)
	private String status;

	@Override
	public CommonPK getId() {
		return new CommonPK(getSeqNo(), getSeafarerId());
	}

}
