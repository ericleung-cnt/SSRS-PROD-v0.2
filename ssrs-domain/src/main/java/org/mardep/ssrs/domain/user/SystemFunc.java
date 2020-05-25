package org.mardep.ssrs.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="SYSTEM_FUNCS")
@NoArgsConstructor
public class SystemFunc extends AbstractPersistentEntity<Long>{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "FUNC_ID", nullable = false)
	@Setter
	private Long id;

	@Getter
	@Setter
	@Column(name = "FUNC_KEY", nullable = false, length=50)
	private String key;
	
	@Getter
	@Setter
	@Column(name = "FUNC_DESC", length=255)
	private String desc;

	@Override
	public Long getId() {
		return id;
	}
}
