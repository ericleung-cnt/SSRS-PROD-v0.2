package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="TRANSACTIONS_CODES")
@NoArgsConstructor
public class TransactionCode extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Column(name = "TXN_CODE", nullable=false, length=2)
	private String id;

	@Getter
	@Setter
	@Column(name = "TC_DESC", length=60)
	private String tcDesc;


	@Override
	public String getId() {
		return id;
	}

}
