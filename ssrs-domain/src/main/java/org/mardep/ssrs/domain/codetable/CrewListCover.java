package org.mardep.ssrs.domain.codetable;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="CREW_LIST_COVER")
@IdClass(CrewListCoverPK.class)
@ToString(of={"vesselId", "coverYymm"})
public class CrewListCover extends AbstractPersistentEntity<CrewListCoverPK> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@Column(name = "VESSEL_ID", nullable=false, length=50) // TODO 50 or 10
	private String vesselId;

	@Id
	@Getter
	@Setter
	@Column(name = "COVER_YYMM", length=6, nullable=false)
	private String coverYymm;
	
	@Getter
	@Setter
	@Column(name = "IMO_NO", length=9)
	private String imoNo;
	
	@Column(name = "COMMENCE_DATE")
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	private Date commenceDate;
	
	@Getter
	@Setter
	@Column(name = "COMMENCE_PLACE", length=30)
	private String commencePlace;

	@Column(name = "TERMINATE_DATE")
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	private Date terminateDate;
	
	@Getter
	@Setter
	@Column(name = "TERMINATE_PLACE", length=30)
	private String terminatePlace;

	@Getter
	@Setter
	@Column(name = "AGREEMENT_PERIOD")
	private BigDecimal agreementPeriod;
	
	@Getter
	@Setter
	@Column(name = "DG_DESC", length=30)
	private String dgDesc;

	@Getter
	@Setter
	@Column(name = "DOC_LOCATION", length=100)
	private String docLocation;
	
	@Override
	public CrewListCoverPK getId() {
		return new CrewListCoverPK(getVesselId(), getCoverYymm());
	}

}
