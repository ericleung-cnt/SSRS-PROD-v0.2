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
@Table(name="LAWYERS")
public class Lawyer extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@Column(name = "LAW_CODE", nullable=false, length=50)
	private String id;

	@Getter
	@Setter
	@Column(name = "LAW_NAME1", nullable=false, length=40)
	private String name;

	@Getter
	@Setter
	@Column(name = "ADDRESS1", length=40)
	private String address1;

	@Getter
	@Setter
	@Column(name = "ADDRESS2", length=40)
	private String address2;

	@Getter
	@Setter
	@Column(name = "ADDRESS3", length=40)
	private String address3;

	@Getter
	@Setter
	@Column(name = "TEL_NO", length=50)
	private String telNo;

	@Getter
	@Setter
	@Column(name = "FAX_NO", length=50)
	private String faxNo;

	@Getter
	@Setter
	@Column(name = "EMAIL", length=50)
	private String email;
	
	/**
	 * alter table lawyers add LAWYER nvarchar(1) not null default 'N'
	 */
	@Getter
	@Setter
	@Column(name="lawyer", length=1)
	private String lawyer;

	@Override
	public String getId() {
		return id;
	}

}
