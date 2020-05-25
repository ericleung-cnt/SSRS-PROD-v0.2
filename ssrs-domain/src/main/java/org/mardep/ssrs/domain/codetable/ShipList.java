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
@Table(name="SHIPLIST")
@NoArgsConstructor
public class ShipList extends AbstractPersistentEntity<Integer> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "SEQ_NO", nullable=false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "VESSEL_NAME", length=40, nullable=false)
	private String vesselName;

	@Getter
	@Setter
	@Column(name = "PART_TYPE", length=1)
	private String partType;

	@Getter
	@Setter
	@Column(name = "COMPANY_NAME", length=50, nullable=false)
	private String companyName;

	@Getter
	@Setter
	@Column(name = "FLAG", length=20, nullable=false)
	private String flag;

	@Getter
	@Setter
	@Column(name = "NO_OF_REG", length=5)
	private Integer noOfReg;

	@Getter
	@Setter
	@Column(name = "NO_OF_EXEMPT", length=5)
	private Integer noOfExempt;

	@Getter
	@Setter
	@Column(name = "NO_OF_FOREIGN", length=5)
	private Integer noOfForeign;

	@Getter
	@Setter
	@Column(name = "REMARK", length=60)
	private String remark;

	@Override
	public Integer getId() {
		return id;
	}

}
