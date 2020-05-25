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
@Table(name="SHIP_MANAGERS")
@NoArgsConstructor
public class ShipManager extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "SHIP_MGR_SEQ_NO", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "SHIP_MGR_NAME", length=120)
	private String shipMgrName;

	@Getter
	@Setter
	@Column(name = "SHIP_MGR_ADDR1", length=80)
	private String addr1;
	
	@Getter
	@Setter
	@Column(name = "SHIP_MGR_ADDR2", length=80)
	private String addr2;
	
	@Getter
	@Setter
	@Column(name = "SHIP_MGR_ADDR3", length=80)
	private String addr3;

	@Getter
	@Setter
	@Column(name = "COMPANY_ID", length=20)
	private String companyId;
	
	@Getter
	@Setter
	@Column(name = "EMAIL", length=50)
	private String email;
	
	@Override
	public Long getId() {
		return id;
	}

}
