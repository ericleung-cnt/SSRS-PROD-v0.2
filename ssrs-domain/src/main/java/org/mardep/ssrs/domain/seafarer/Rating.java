package org.mardep.ssrs.domain.seafarer;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.Rank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="SEAFARER_RATING")
@IdClass(CommonPK.class)
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"seafarerId", "seqNo"})
public class Rating extends AbstractPersistentEntity<CommonPK> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@Column(name = "SEQ_NO", nullable=false, length=5)
	private Integer seqNo;

	@Id
	@Getter
	@Setter
	@Column(name = "SEAFARER_ID", nullable=false, length=20)
	private String seafarerId;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SF_SFR_FK"))
	private Seafarer seafarer;

	@Getter
	@Setter
	@Column(name = "RATING", length=40)
	private String rating;
	
	@Getter
	@Setter
	@Column(name = "salary", precision=8, scale=0)
	private BigDecimal salary; 

	@Getter
	@Setter
	@Column(name = "RATING_DATE")
	@Temporal(TemporalType.DATE)
	private Date ratingDate; 

	@Getter
	@Setter
	@Column(name = "CAPACITY_ID", nullable=false)
	private Long capacityId;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CAPACITY_ID", referencedColumnName="CAPACITY_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="FK_RATING_CAPACITY_ID"))
	private Rank rank;
	// TODO no FK here
	
	@Override
	public CommonPK getId() {
		return new CommonPK(getSeqNo(), getSeafarerId());
	}

}
