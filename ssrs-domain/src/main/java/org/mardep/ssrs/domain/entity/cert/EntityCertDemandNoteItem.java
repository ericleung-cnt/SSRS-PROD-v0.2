package org.mardep.ssrs.domain.entity.cert;

import java.math.BigDecimal;

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
@Table(name="CERT_DEMAND_NOTE_ITEM")
public class EntityCertDemandNoteItem  extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "ITEM_ID", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long itemId;

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

	@Getter
	@Setter
	@Column(name = "FC_FEE_CODE", length=50)
	private String fcFeeCode;

	@Getter
	@Setter
	@Column(name = "AMOUNT", nullable=false, precision=10, scale=2)
	private BigDecimal amount;
	
	
	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return itemId;
	}

	
}
