package org.mardep.ssrs.domain.sr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
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
@Table(name="REPRESENTATIVES")
@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo"})
@EntityListeners(SrEntityListener.class)
public class Representative extends AbstractPersistentEntity<String> implements Cloneable {

	private static final long serialVersionUID = 1L;

	public static final String STATUS_CORPORATION = "C";
	public static final String STATUS_INDIVIDUAL = "I";

	@Id
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false),
    }, foreignKey=@ForeignKey(name="REPRESENTATIVE_RM_FK")
	)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private RegMaster regMaster;

	@Getter
	@Setter
	@Column(name = "STATUS", length=1, nullable=false)
	private String status; // enum

	@Getter
	@Setter
	@Column(name = "REP_NAME1", length=160, nullable=false)
	private String name;

	@Getter
	@Setter
	@Column(name = "HKIC", length=9)
	private String hkic;

	@Getter
	@Setter
	@Column(name = "INCORP_CERT", length=15)
	private String incorpCert;

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
	@Column(name = "TEL_NO", length=18)
	private String telNo;

	@Getter
	@Setter
	@Column(name = "FAX_NO", length=18)
	private String faxNo;

	@Getter
	@Setter
	@Column(name = "EMAIL", length=50)
	private String email;

	@Getter
	@Setter
	@Column(name = "TELEX_NO", length=18)
	private String telex;
	
	@Override
	public String getId() {
		return getApplNo();
	}

	@Override
	public Representative clone() {
		try {
			Representative result = (Representative) super.clone();
			result.setVersion(null);
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

	}

}
