package org.mardep.ssrs.domain.codetable;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
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
@Table(name="CREW")
@IdClass(CrewPK.class)
@ToString(of={"vesselId", "coverYymm", "referenceNo"})
public class Crew extends AbstractPersistentEntity<CrewPK> {

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

	@Id
	@Getter
	@Setter
	@Column(name = "REFERENCE_NO", nullable=false)
	private Integer referenceNo;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
			@JoinColumn(name="VESSEL_ID", referencedColumnName="VESSEL_ID", updatable=false, insertable=false),
			@JoinColumn(name="COVER_YYMM", referencedColumnName="COVER_YYMM", updatable=false, insertable=false)
	    }, foreignKey=@ForeignKey(name="CREW_CLC_FK")
		)
	private CrewListCover crewListCover;

	@Getter
	@Setter
	@Column(name = "SEAFARER_NAME", length=30)
	private String seafarerName;

	@Getter
	@Setter
	@Column(name = "SERB_NO", length=15)
	private String serbNo;

	@Getter
	@Setter
	@Column(name = "NATIONALITY_ID", nullable=false)
	private Long nationalityId;
//	TODO no FK here
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NATIONALITY_ID", referencedColumnName="NATIONALITY_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="C_NATIONALITY_FK"))
	private Nationality nationality;


	@Column(name = "BIRTH_DATE", nullable=false)
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	private Date birthDate;

	@Getter
	@Setter
	@Column(name = "BIRTH_PLACE", length=30)
	private String birthPlace;

	@Getter
	@Setter
	@Column(name = "CREW_ADDRESS1", length=50)
	private String address1;

	@Getter
	@Setter
	@Column(name = "CREW_ADDRESS2", length=50)
	private String address2;

	@Getter
	@Setter
	@Column(name = "CREW_ADDRESS3", length=50)
	private String address3;

	@Getter
	@Setter
	@Column(name = "NOK_NAME", length=30)
	private String nokName;

	@Getter
	@Setter
	@Column(name = "CAPACITY_ID", nullable=false)
	private Long capacityId;
//	TODO no FK here

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CAPACITY_ID", referencedColumnName="CAPACITY_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="C_R_FK"))
	private Rank rank;

	@Getter
	@Setter
	@Column(name = "CREW_CERT", length=30)
	private String crewCert;

	@Getter
	@Setter
	@Column(name = "SALARY", nullable=false)
	private BigDecimal salary;

	@Column(name = "ENGAGE_DATE", nullable=false)
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	protected Date engageDate;

	@Getter
	@Setter
	@Column(name = "ENGAGE_PLACE", length=30)
	private String engagePlace;

	@Column(name = "DISCHARGE_DATE")
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	protected Date dischargeDate;

	@Getter
	@Setter
	@Column(name = "DISCHARGE_PLACE", length=30)
	private String dischargePlace;

	@Getter
	@Setter
	@Column(name = "STATUS", length=1)
	private String status;// TODO enum ?

	@Getter
	@Setter
	@Column(name = "R_SALARY")
	private BigDecimal rSalary;

	@Getter
	@Setter
	@Column(name = "CURRENCY", length=5)
	private String currency;


	@Override
	public CrewPK getId() {
		return new CrewPK(getVesselId(), getCoverYymm(), getReferenceNo());
	}

}
