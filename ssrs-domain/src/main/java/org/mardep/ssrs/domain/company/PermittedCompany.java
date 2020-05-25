package org.mardep.ssrs.domain.company;

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
@Table(name="PERMITTED_COMPANIES")
public class PermittedCompany extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Column(name = "PERMITTED_COMPANY_CODE", nullable=false, length=50)
	private String id;

	@Getter
	@Setter
	@Column(name = "COMPANY_NAME", length=100)
	private String name1; 

	@Getter
	@Setter
	@Column(name = "COMPANY_EMAIL", length=100)
	private String email; 

	@Getter
	@Setter
	@Column(name = "COMPANY_ADDR1", length=100)
	private String addr1;

	@Getter
	@Setter
	@Column(name = "COMPANY_ADDR2", length=100)
	private String addr2;
	
	@Getter
	@Setter
	@Column(name = "COMPANY_ADDR3", length=100)
	private String addr3;
	
	@Override
	public String getId() {
		return id;
	}

}
