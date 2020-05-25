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
@Table(name="NATIONALITY")
@NoArgsConstructor
public class Nationality extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "NATIONALITY_ID", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "ENG_DESC", nullable=false, length=50)
	private String engDesc;

	@Getter
	@Setter
	@Column(name = "CHI_DESC", length=20)
	private String chiDesc;

	@Getter
	@Setter
	@Column(name = "COUNTRY_ENG_DESC", nullable=false, length=50)
	private String countryEngDesc;

	@Getter
	@Setter
	@Column(name = "COUNTRY_CHI_DESC", length=20)
	private String countryChiDesc;

	@Override
	public Long getId() {
		return id;
	}

}
