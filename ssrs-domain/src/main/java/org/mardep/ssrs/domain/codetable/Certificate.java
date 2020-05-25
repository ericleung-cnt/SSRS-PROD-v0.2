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
@Table(name="APPROVED_CERTIFICATES")
public class Certificate extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Column(name = "APPROVED_CERT_CODE", nullable=false, length=50)
	private String id;

	@Getter
	@Setter
	@Column(name = "CERT_NAME", length=50)
	private String name;

	@Getter
	@Setter
	@Column(name = "CERT_DESC", length=100)
	private String desc;

	@Override
	public String getId() {
		return id;
	}

}
