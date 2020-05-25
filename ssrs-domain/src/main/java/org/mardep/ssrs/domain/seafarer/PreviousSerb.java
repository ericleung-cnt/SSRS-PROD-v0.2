package org.mardep.ssrs.domain.seafarer;

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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="PREVIOUS_SERB")
@IdClass(CommonPK.class)
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"seafarerId", "seqNo"})
public class PreviousSerb extends AbstractPersistentEntity<CommonPK> {

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
	@JoinColumn(name="SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="PS_PSS_FK"))
	private Seafarer seafarer;

	@Getter
	@Setter
	@Column(name = "SERB_NO", length=10)
	private String serbNo;
	
	/**
	 * column data from Oracle, and it store end date, to MSSQL, create another column 'startDate'
	 * 
	 */
	@Getter
	@Setter
	@Column(name = "SERB_DATE")
	@Temporal(TemporalType.DATE)
	private Date serbDate;
	
	@Getter
	@Setter
	@Column(name = "SERB_START_DATE")
	@Temporal(TemporalType.DATE)
	private Date serbStartDate;
	
	@Getter
	@Setter
	@Column(name = "REMARKS", length=255)
	private String remarks;
	
 
	
	@Override
	public CommonPK getId() {
		return new CommonPK(getSeqNo(), getSeafarerId());
	}

}
