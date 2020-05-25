package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Entity
@Table(name="CLASS_SOCIETIES")
public class ClassSociety extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Column(name = "CLASS_SOC_CODE", nullable=false, length=50)
	private String id;

	@Getter
	@Setter
	@Column(name = "CLASS_SOC_NAME", nullable=false, length=50)
	private String name;

	@Override
	public String getId() {
		return id;
	}

}
