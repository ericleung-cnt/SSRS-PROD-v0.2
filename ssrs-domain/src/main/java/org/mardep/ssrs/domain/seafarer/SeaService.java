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
import javax.persistence.Transient;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.MmoShipType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="SEA_SERVICE")
@IdClass(CommonPK.class)
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"seafarerId", "seqNo"})
public class SeaService extends AbstractPersistentEntity<CommonPK> {

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
	@JoinColumn(name="SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SF_SFS_FK"))
	private Seafarer seafarer;

	@Getter
	@Setter
	@Column(name = "VESSEL_NAME", nullable=false, length=40)
	private String vesselName;

	/**
	 * <li>1 for SEA GOING<br>
	 * <li>2 for RIVER TRADE
	 */
	@Getter
	@Setter
	@Column(name = "VOYAGE_TYPE", length=2)
	private String voyageType;

	@Getter
	@Setter
	@Column(name = "FLAG", length=20)
	private String flag;

	@Getter
	@Setter
	@Column(name = "SHIP_TYPE", length=50)
	private String shipTypeCode; // TODO length not match

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SHIP_TYPE", referencedColumnName="CODE", updatable=false, insertable=false)
	@Getter
	@Setter
	private MmoShipType shipType;

	@Getter
	@Setter
	@Column(name = "CAPACITY", length=30)
	private String capacity; // TODO refer to RANK ?

	@Getter
	@Setter
	@Column(name = "EMPLOYMENT_DATE")
	@Temporal(TemporalType.DATE)
	private Date employmentDate;

	@Getter
	@Setter
	@Column(name = "EMPOYMENT_PLACE", length=30)
	private String empoymentPlace;

	@Getter
	@Setter
	@Column(name = "DISCHARGE_DATE")
	@Temporal(TemporalType.DATE)
	private Date dischargeDate;

	@Getter
	@Setter
	@Column(name = "DISCHARGE_PLACE", length=30)
	private String dischargePlace;

	@Getter
	@Setter
	@Column(name = "REPORT_DATE")
	@Temporal(TemporalType.DATE)
	private Date reportDate;

	@Getter
	@Setter
	@Column(name = "DCHPEND", length=1)
	private String dchpend; // Report Dischange

	@Getter
	@Setter
	@Column(name = "DI_DATE")
	@Temporal(TemporalType.DATE)
	private Date diDate;
	
	@Transient
	private boolean reportDischarge;
	@Transient
	public boolean isReportDischarge() {
		try{
			return BooleanUtils.toBoolean(dchpend);
		}catch(Exception ex){
			return false;
		}
	}
	
	@Override
	public CommonPK getId() {
		return new CommonPK(getSeqNo(), getSeafarerId());
	}




}
