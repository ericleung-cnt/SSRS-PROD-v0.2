package org.mardep.ssrs.domain.sr;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="OWNERS")
@IdClass(OwnerPK.class)
@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "ownerSeqNo"})
@EntityListeners(SrEntityListener.class)
public class Owner extends AbstractPersistentEntity<OwnerPK> implements Cloneable {

	private static final long serialVersionUID = 1L;

	public static final String TYPE_DEMISE = "D";

	@Id
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	@Id
	@Getter
	@Setter
	@Column(name = "OWNER_SEQ_NO", nullable=false)
	private Integer ownerSeqNo;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false),
    }, foreignKey=@ForeignKey(name="OWR_RMR_FK")
	)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private RegMaster regMaster;


	@Getter
	@Setter
	@Column(name = "OWNER_NAME", length=160, nullable=false)
	private String name;

	@Getter
	@Setter
	@Column(name = "OWNER_TYPE", length=1)
	private String type; // TODO enum

	@Getter
	@Setter
	@Column(name = "STATUS", length=1)
	private String status; // TODO enum

	@Getter
	@Setter
	@Column(name = "QUALIFIED", length=1)
	private String qualified; // TODO enum

	@Getter
	@Setter
	@Column(name = "NATION_PASSPORT", length=50)
	private String nationPassport;

	@Getter
	@Setter
	@Column(name = "ADDRESS1", length=80)
	private String address1;

	@Getter
	@Setter
	@Column(name = "ADDRESS2", length=80)
	private String address2;

	@Getter
	@Setter
	@Column(name = "ADDRESS3", length=80)
	private String address3;

	@Getter
	@Setter
	@Column(name = "EMAIL", length=50)
	private String email;

	@Getter
	@Setter
	@Column(name = "INT_MIXED")
	private BigDecimal intMixed;

	@Getter
	@Setter
	@Column(name = "INT_NUMBERATOR")
	private Long intNumberator;

	@Getter
	@Setter
	@Column(name = "INT_DENOMINATOR")
	private Long intDenominator;

	@Getter
	@Setter
	@Column(name = "HKIC", length=9)
	private String hkic;

	@Getter
	@Setter
	@Column(name = "OCCUPATION", length=20)
	private String occupation;

	@Getter
	@Setter
	@Column(name = "INCORT_CERT", length=15)
	private String incortCert;

	@Getter
	@Setter
	@Column(name = "OVERSEA_CERT", length=15)
	private String overseaCert;

	@Getter
	@Setter
	@Column(name = "INCORP_PLACE", length=30)
	private String incorpPlace;

	@Getter
	@Setter
	@Column(name = "REG_PLACE", length=30)
	private String regPlace;

	@Getter
	@Setter
	@Column(name = "CHARTER_SDATE")
	private Date charterSdate;

	@Getter
	@Setter
	@Column(name = "CHARTER_EDATE")
	private Date charterEdate;

	@Getter
	@Setter
	@Column(name = "MAJOR_OWNER")
	private Boolean majorOwner;

	@Getter
	@Setter
	@Column(name = "CORR_ADDR1", length=40)
	private String corrAddr1;

	@Getter
	@Setter
	@Column(name = "CORR_ADDR2", length=40)
	private String corrAddr2;

	@Getter
	@Setter
	@Column(name = "CORR_ADDR3", length=40)
	private String corrAddr3;

	@Override
	public OwnerPK getId() {
		return new OwnerPK(getApplNo(), getOwnerSeqNo());
	}

	@Override
	public Owner clone()  {
		try {
			Owner result = (Owner) super.clone();
			result.setVersion(null);
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
