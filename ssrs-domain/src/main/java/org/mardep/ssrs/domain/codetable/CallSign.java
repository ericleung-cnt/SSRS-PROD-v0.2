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
@Table(name="CALL_SIGNS")
@IdClass(CallSignPK.class)
@ToString(of={"csCallSign", "csAvailFlat"})
public class CallSign extends AbstractPersistentEntity<CallSignPK> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@Column(name = "CS_CALL_SIGN", nullable=false, length=7)
	private String csCallSign;

	@Id
	@Getter
	@Setter
	@Column(name = "CS_AVAIL_FLAT", nullable=false, length=1)
	private String csAvailFlat; 

	@Override
	public CallSignPK getId() {
		return new CallSignPK(getCsCallSign(), getCsAvailFlat());
	}

}
