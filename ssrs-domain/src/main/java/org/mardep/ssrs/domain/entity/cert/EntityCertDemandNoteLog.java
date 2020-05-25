package org.mardep.ssrs.domain.entity.cert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="CERT_DEMAND_NOTE_LOG")
public class EntityCertDemandNoteLog  extends AbstractPersistentEntity<Integer>{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "LOG_ID", nullable=false)
	private Integer logId;

	@Getter
	@Setter	
	@Column(name = "CERT_APPLICATION_ID")
	private Integer certApplicationId;
	
	@Getter
	@Setter
	@Column(name = "CERT_APPLICATION_NO", length=9)
	private String certApplicationNo;

	@Getter
	@Setter
	@Column(name = "CERT_TYPE", length=50)
	private String certType;

	@Getter
	@Setter
	@Column(name = "DEMAND_NOTE_NO", length=30)
	private String demandNoteNo;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return logId;
	}

}
