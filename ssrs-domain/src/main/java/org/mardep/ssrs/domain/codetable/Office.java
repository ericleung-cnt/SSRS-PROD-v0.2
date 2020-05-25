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


@Entity
@Table(name="OFFICE")
@NoArgsConstructor
public class Office extends AbstractPersistentEntity<Integer> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "OFFICE_ID", nullable=false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "OFFICE_CODE", length=10, nullable=true)
	private String code;
	
	@Getter
	@Setter
	@Column(name = "DN_CODE", length=10, nullable=true)
	private String dnCode;
	
	@Getter
	@Setter
	@Column(name = "OFFICE_NAME", length=80, nullable=false)
	private String name;
	
	@Getter
	@Setter
	@Column(name = "OFFICE_ADDR_1", length=50)
	private String address1;
	
	@Getter
	@Setter
	@Column(name = "OFFICE_ADDR_2", length=50)
	private String address2;

	@Getter
	@Setter
	@Column(name = "OFFICE_ADDR_3", length=50)
	private String address3;
	
	@Getter
	@Setter
	@Column(name = "OFFICE_EMAIL", length=50)
	private String email;
	
	@Getter
	@Setter
	@Column(name = "OFFICE_TEL", length=18)
	private String tel;
	
	@Override
	public Integer getId() {
		return id;
	}

}
