package org.mardep.ssrs.domain.sr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
@Table(name="MORTGAGORS")
@IdClass(MortgagorPK.class)
@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "priorityCode", "seq"})
public class Mortgagor extends AbstractPersistentEntity<MortgagorPK> {

	private static final long serialVersionUID = 4694374539370920437L;

	@Id
	@Getter
	@Setter
	@Column(name = "MGR_RM_APPL_NO", nullable=false, length=9)
	private @NonNull String applNo;

	@Id
	@Getter
	@Setter
	@Column(name = "MGR_MOR_PRIORITY_CODE", nullable=false, length=2)
	private @NonNull String priorityCode;

	@Id
	@Getter
	@Setter
	@Column(name = "MGR_OWN_OWNER_SEQ_NO", nullable=false)
	private @NonNull Integer seq;

	@Getter
	@Setter
	@JoinColumns(value={
			@JoinColumn(name="MGR_RM_APPL_NO",referencedColumnName="RM_APPL_NO",insertable=false, updatable=false),
			@JoinColumn(name="MGR_OWN_OWNER_SEQ_NO",referencedColumnName="OWNER_SEQ_NO",insertable=false, updatable=false)
	})
	@ManyToOne(fetch=FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	private Owner owner;

	@Override
	public MortgagorPK getId() {
		return new MortgagorPK(applNo, priorityCode, seq);
	}


}
