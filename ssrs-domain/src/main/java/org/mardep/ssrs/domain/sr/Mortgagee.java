package org.mardep.ssrs.domain.sr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="MORTGAGEES")
@IdClass(MortgageePK.class)
@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "priorityCode", "seq"})
public class Mortgagee extends AbstractPersistentEntity<MortgageePK> {

	private static final long serialVersionUID = -7447864450400351641L;

	@Id
	@Getter
	@Setter
	@Column(name = "MOR_APPL_NO", nullable=false, length=9)
	private @NonNull String applNo;

	@Id
	@Getter
	@Setter
	@Column(name = "MOR_PRIORITY_CODE", nullable=false, length=2)
	private @NonNull String priorityCode;

	@Id
	@Getter
	@Setter
	@Column(name = "MGE_SEQ_NO", nullable=false, length=2)
	private @NonNull Integer seq;

	@Getter
	@Setter
	@Column(name = "NAME", nullable=false, length=80)
	private String name;

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

	@Override
	public MortgageePK getId() {
		return new MortgageePK(applNo, priorityCode, seq);
	}

}
