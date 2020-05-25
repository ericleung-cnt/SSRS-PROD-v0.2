package org.mardep.ssrs.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Deprecated
public class Func extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@Column(name = "FUNC_ID", nullable=false, length=10)
	private String id;

	@Getter
	@Setter
	@Column(name = "FUNC_DESC", length=200, nullable=false)
	private String funcDesc;

	@Getter
	@Setter
	@Column(name = "MENU_ID", length=40, nullable=false)
	private String menuId;

	@Override
	public String getId() {
		return id;
	}

}
