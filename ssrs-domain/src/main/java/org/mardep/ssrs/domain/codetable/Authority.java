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
@Table(name="APPROVED_CERTIFICATE_ISSUING_AUTHORITIES")
public class Authority extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Column(name = "APPROVED_ISSUING_AUTHORITY_CODE", nullable=false, length=50)
	private String id;

	@Getter
	@Setter
	@Column(name = "ISSUING_AUTHORITY_NAME", length=50)
	private String name;

	@Getter
	@Setter
	@Column(name = "ISSUING_AUTHORITY_DESC", length=100)
	private String desc;
	
	@Getter
	@Setter
	@Column(name = "SEQ")
	private Integer seq;

	@Override
	public String getId() {
		return id;
	}

}
