package org.mardep.ssrs.domain.user;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Deprecated
//@Entity
@Table(name="STAFF")
@NoArgsConstructor
public class Staff extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@Column(name = "STAFF_NO", nullable=false, length=15)
	private String id;

	@Getter
	@Setter
	@Column(name = "STAFF_ID", length=80, nullable=false)
	private String staffId;

	@Getter
	@Setter
	@Column(name = "NAME", length=60)
	private String NAME;

	@Getter
	@Setter
	@Column(name = "CNAME", length=20)
	private String chiName;

	@Getter
	@Setter
	@Column(name = "POST_CODE", length=40)
	private String postCode;

	@Getter
	@Setter
	@Column(name = "SERVE")
	private Boolean serve;

	@Override
	public String getId() {
		return id;
	}

}
