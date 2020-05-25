package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//@Entity
//@Table(name="VESSEL")
//@NoArgsConstructor
@Deprecated
public class Vessel extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Column(name = "VESSEL_ID", nullable=false, length=50)
	private String id;

	@Getter
	@Setter
	@Column(name = "VESSEL_NAME", length=40)
	private String name;

	@Getter
	@Setter
	@Column(name = "VESSEL_CHI_NAME", length=60)
	private String chiName;

	@Getter
	@Setter
	@Column(name = "VESSEL_TYPE", length=3)
	private String vesselType;

	@Getter
	@Setter
	@Column(name = "STATUS", length=1)
	private String status; // TODO enum


	@Override
	public String getId() {
		return id;
	}

}
