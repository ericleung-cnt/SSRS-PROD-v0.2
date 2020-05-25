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
@Table(name="SYSTEM_PARAMS")
public class SystemParam extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@Column(name = "ID", nullable=false)
	private String id;

	@Getter
	@Setter
	@Column(name = "VALUE", nullable=false, length=4096)
	private String value;

	@Getter
	@Setter
	@Column(name = "REMARK", length=100)
	private String remark;

	@Override
	public String getId() {
		return id;
	}

}
