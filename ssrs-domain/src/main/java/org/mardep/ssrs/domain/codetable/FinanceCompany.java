package org.mardep.ssrs.domain.codetable;

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


@NoArgsConstructor
@Entity
@Table(name="FINANCE_COMPANIES")
public class FinanceCompany extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@Column(name = "COMPANY_SEQ_NO", nullable=false, length=50)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Setter
	@Column(name = "COMPANY_NAME1", length=80)
	private String name;

	@Getter
	@Setter
	@Column(name = "ADDRESS1", length=100)
	private String addr1;

	@Getter
	@Setter
	@Column(name = "ADDRESS2", length=100)
	private String addr2;

	@Getter
	@Setter
	@Column(name = "ADDRESS3", length=100)
	private String addr3;

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
	@Column(name = "TELIX", length=50)
	private String telix;

	@Getter
	@Setter
	@Column(name = "EMAIL", length=50)
	private String email;

	@Getter
	@Setter
	@Column(name = "COMPANY_TYPE", length=1)
	private String companyType;

	@Override
	public Long getId() {
		return id;
	}

}
