package org.mardep.ssrs.domain.sr;

import java.math.BigDecimal;
import java.util.Date;

import javax.management.RuntimeErrorException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.Agent;
import org.mardep.ssrs.domain.codetable.Country;
import org.mardep.ssrs.domain.codetable.OperationType;
import org.mardep.ssrs.domain.codetable.ShipSubType;
import org.mardep.ssrs.domain.codetable.ShipType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="EBS_TRANSCRIPTS")
@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"txnCode", "applNo"})
public class EbsTranscript extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 2449735922720330358L;

	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "TXN_CODE", nullable=false, length=15)
	private Long txnCode;

	@Getter
	@Setter
	private String applNo;

	@Getter
	@Setter
	private String billingPerson;

	@Getter
	@Setter
	private BigDecimal amount;

	@Getter
	@Setter
	private String feeCode;

	@Override
	public Long getId() {
		return txnCode;
	}

}
