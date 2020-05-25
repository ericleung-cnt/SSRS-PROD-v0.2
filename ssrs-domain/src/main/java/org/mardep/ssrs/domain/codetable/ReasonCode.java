package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="REASON_CODES")
@IdClass(ReasonCodePK.class)
@ToString(of={"reasonCode", "reasonType"})
public class ReasonCode extends AbstractPersistentEntity<ReasonCodePK> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@Column(name = "REASON_CODE", nullable=false, length=50)
	private String reasonCode;

	@Id
	@Getter
	@Setter
	@Column(name = "REASON_TYPE", nullable=false, length=50)
	private String reasonType; 

	@Getter
	@Setter
	@Column(name = "ENGLISH_DESC", nullable=false, length=80)
	private String engDesc;

	@Getter
	@Setter
	@Column(name = "CHINESE_DESC", length=100)
	private String chiDesc;

	@Override
	public ReasonCodePK getId() {
		return new ReasonCodePK(getReasonCode(), getReasonType());
	}

}
