package org.mardep.ssrs.domain.sr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Deprecated
@NoArgsConstructor
@Entity
@Table(name="CSR_FORM_PROGRESS")
@IdClass(CsrFormPK.class)
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"imoNo", "formSeq"})
public class CsrFormProgress extends AbstractPersistentEntity<CsrFormPK> {

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
	@Column(name = "SHIP_NAME", length=70)
	private String shipName;
	
	@Getter
	@Setter
	@Column(name = "TYPE_OF_CHANGE", length=1)
	private String typeOfChange; // TODO enum ?

	@Getter
	@Setter
	@Column(name = "CHANGE_DATE")
	@Temporal(TemporalType.DATE)
	private Date changeDate;
	
	// TODO more fields

	@Override
	public CsrFormPK getId() {
		return new CsrFormPK(getImoNo(), getFormSeq());
	}

}
