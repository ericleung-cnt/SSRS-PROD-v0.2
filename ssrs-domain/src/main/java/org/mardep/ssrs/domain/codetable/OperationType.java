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
@Table(name="OPERATION_TYPES")
@NoArgsConstructor
public class OperationType extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Column(name = "OPER_TYPE_CODE", nullable=false, length=3)
	private String id;

	@Getter
	@Setter
	@Column(name = "OT_DESC", nullable=false, length=40)
	private String otDesc;


	@Override
	public String getId() {
		return id;
	}

}
