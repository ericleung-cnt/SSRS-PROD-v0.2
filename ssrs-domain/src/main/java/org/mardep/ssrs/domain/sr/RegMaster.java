package org.mardep.ssrs.domain.sr;

import java.math.BigDecimal;
import java.util.Date;

import javax.management.RuntimeErrorException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
@Table(name="REG_MASTERS")
@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "applNoSuf"})
@EntityListeners(SrEntityListener.class)
public class RegMaster extends AbstractPersistentEntity<String> implements Cloneable {

	private static final long serialVersionUID = 1L;

	public static final String REG_STATUS_ACTIVE = "A";

	public static final String REG_STATUS_APPLICATION = "A";
	public static final String REG_STATUS_REGISTERED = "R";
	public static final String REG_STATUS_WITHDRAW = "W";

	public static final String REG_STATUS_DEREGISTERED = "D";

	public static final String REG_REGN_TYPE_TRANSITIONAL = "O";

	public static final String REG_REGN_TYPE_NON_TRANSITIONAL = "N";

	@Id
	@Getter
	@Setter
	@Column(name = "APPL_NO", nullable=false, length=9)
	private String applNo;

	@Getter
	@Setter
	@Column(name = "APPL_NO_SUF", length=1)
	private String applNoSuf;

	@Getter
	@Setter
	@Column(name = "COR_COLLECT")
	private String corCollect;
	
//	@Getter
//	@Setter
//	@OneToOne(fetch=FetchType.LAZY)
//	@Audited(targetAuditMode = NOT_AUDITED)
//	@JoinColumn(name="COR_COLLECT", referencedColumnName="OFFICE_NO", updatable=false, insertable=false, foreignKey=@ForeignKey(name="USER_FC_FK"))
//	private Office office;
	
	
	
	@Getter
	@Setter
	@Column(name = "ENG_MAKER", length=1)
	private String engMaker;

	@Getter
	@Setter
	@Column(name = "REG_STATUS", length=1)
	private String regStatus; //TODO enum  D/R

	@Getter
	@Setter
	@Column(name = "REG_REGN_TYPE", length=1)
	private String regRegnType; //TODO enum  A/X

	@Getter
	@Setter
	@Column(name = "REG_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date regDate;

	@Getter
	@Setter
	@Column(name = "PROV_EXP_DATE")
	@Temporal(TemporalType.DATE)
	private Date provExpDate;

	@Getter
	@Setter
	@Column(name = "DEREG_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deRegTime;

	@Getter
	@Setter
	@Column(name = "ATF_DUE_DATE")
	@Temporal(TemporalType.DATE)
	private Date atfDueDate;

	@Getter
	@Setter
	@Column(name = "BUILD_DATE", length=35)
	private String buildDate; // TODO date or nvarchar

	@Getter
	@Setter
	@Column(name = "BUILD_YEAR")
	private Integer buildYear;

	@Getter
	@Setter
	@Column(name = "OFF_NO", length=9)
	private String offNo;

	@Getter
	@Setter
	@Column(name = "OFF_RESV_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date offResvDate;

	@Getter
	@Setter
	@Column(name = "CALL_SIGN", length=7)
	private String callSign;

	@Getter
	@Setter
	@Column(name = "CS_RESV_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date csResvDate;

	@Getter
	@Setter
	@Column(name = "CS_RELEASE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date csReleaseDate;

	@Getter
	@Setter
	@Column(name = "FIRST_REG_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date firstRegDate;

	@Getter
	@Setter
	@Column(name = "REG_NAME", length=70)
	private String regName;

	@Getter
	@Setter
	@Column(name = "REG_CNAME", length=20)
	private String regChiName;

	@Getter
	@Setter
	@Column(name = "INT_TOT")
	private Integer intTot;

	@Getter
	@Setter
	@Column(name = "SURVEY_SHIP_TYPE", length=60)
	private String surveyShipType;

	@Getter
	@Setter
	@Column(name = "INT_UNIT", length=1)
	private String intUnit;

	@Getter
	@Setter
	@Column(name = "MATERIAL", length=35)
	private String material;

	@Getter
	@Setter
	@Column(name = "NO_OF_SHAFTS")
	private Integer noOfShafts;

	@Getter
	@Setter
	@Column(name = "HOW_PROP", length=45)
	private String howProp;

	@Getter
	@Setter
	@Column(name = "EST_SPEED", length=15)
	private String estSpeed;

	@Getter
	@Setter
	@Column(name = "GROSS_TON")
	private BigDecimal grossTon;

	@Getter
	@Setter
	@Column(name = "REG_NET_TON")
	private BigDecimal regNetTon;

	@Getter
	@Setter
	@Column(name = "TRANSIT_IND", length=1)
	private String transitInd; // TODO enum

	@Getter
	@Setter
	@Column(name = "IMO_NO", length=9)
	private String imoNo;

	@Getter
	@Setter
	@Column(name = "LENGTH")
	private BigDecimal length; // TODO

	@Getter
	@Setter
	@Column(name = "BREADTH")
	private BigDecimal breadth; // TODO

	@Getter
	@Setter
	@Column(name = "DEPTH")
	private BigDecimal depth; // TODO

	@Getter
	@Setter
	@Column(name = "DIM_UNIT", length=1)
	private String dimUnit;

	@Getter
	@Setter
	@Column(name = "ENG_DESC1", length=40)
	private String engDesc1;

	@Getter
	@Setter
	@Column(name = "ENG_DESC2", length=40)
	private String engDesc2;

	@Getter
	@Setter
	@Column(name = "ENG_MODEL_1", length=40)
	private String engModel1;

	@Getter
	@Setter
	@Column(name = "ENG_MODEL_2", length=40)
	private String engModel2;

	@Getter
	@Setter
	@Column(name = "ENG_POWER", length=40)
	private String engPower;

	@Getter
	@Setter
	@Column(name = "ENG_SET_NUM")
	private Integer engSetNum;

	@Getter
	@Setter
	@Column(name = "GEN_ATF_INVOICE", length=1)
	private String genAtfInvoice;

	@Getter
	@Setter
	@Column(name = "REMARK", length=80)
	private String remark;

	@Getter
	@Setter
	@Column(name = "AGT_AGENT_CODE", length=3)
	private String agtAgentCode;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="AGT_AGENT_CODE", referencedColumnName="AGENT_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="RM_AGT_FK"))
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	private Agent agent;

	@Getter
	@Setter
	@Column(name = "CC_COUNTRY_CODE", length=3)
	private String ccCountryCode;

	/**
	 * alter table REG_MASTERS 		add AGENT_COUNTRY_CODE nvarchar(3);
	 * alter table REG_MASTERS_AUD 	add AGENT_COUNTRY_CODE nvarchar(3);
	 * alter table REG_MASTERS_HIST add AGENT_COUNTRY_CODE nvarchar(3);
	 */
	@Getter
	@Setter
	@Column(name = "AGENT_COUNTRY_CODE", length=3)
	private String agentCountryCode;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CC_COUNTRY_CODE", referencedColumnName="COUNTRY_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="RM_CC_FK"))
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	private Country country; // TODO no PK Here

	@Getter
	@Setter
	@Column(name = "OT_OPER_TYPE_CODE", length=3)
	private String operationTypeCode;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OT_OPER_TYPE_CODE", referencedColumnName="OPER_TYPE_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="RM_OT_FK"))
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	private OperationType operationType;

	@Getter
	@Setter
	@Column(name = "RC_REASON_CODE", length=2)
	private String rcReasonCode;

//	@Getter
//	@Setter
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="RC_REASON_CODE", referencedColumnName="REASON_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="RM_RC_FK"))
//	private ReasonCode reasonCode; // TODO no PK Here, and length not match

	@Getter
	@Setter
	@Column(name = "RC_REASON_TYPE", length=1)
	private String rcReasonType; // TODO

	@Getter
	@Setter
	@Column(name = "SS_ST_SHIP_TYPE_CODE", length=50)
	private String shipTypeCode;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SS_ST_SHIP_TYPE_CODE", referencedColumnName="SHIP_TYPE_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="RM_ST_FK"))
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	private ShipType shipType;

	@Getter
	@Setter
	@Column(name = "SS_SHIP_SUBTYPE_CODE", length=50)
	private String shipSubtypeCode;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
			@JoinColumn(name="SS_ST_SHIP_TYPE_CODE", referencedColumnName="ST_SHIP_TYPE_CODE", updatable=false, insertable=false),
			@JoinColumn(name="SS_SHIP_SUBTYPE_CODE", referencedColumnName="SHIP_SUBTYPE_CODE", updatable=false, insertable=false)
	    }, foreignKey=@ForeignKey(name="RM_SS_FK")
		)
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	private ShipSubType shipSubType;

	@Getter
	@Setter
	@Column(name = "LICENSE_NO", length=20)
	private String licenseNo;

	@Getter
	@Setter
	@Column(name="EPAYMENT_INDICATOR", length=1)
	private String epayment;

	@Getter
	@Setter
	@Column(name="DETAIN_STATUS", length=1)
	private String detainStatus;

	@Getter
	@Setter
	@Column(name="DETAIN_DESC", length=160)
	private String detainDesc;
	@Getter
	@Setter
	@Column(name="IMO_OWNER_ID", length=20)
	private String imoOwnerId;
	@Getter
	@Setter
	@Column(name="DERATED_ENGINE_POWER", length=40)
	private String deratedEnginePower;
	@Getter
	@Setter
	@Column(name="TRACK_CODE", length=20)
	private String trackCode;
	@Getter
	@Setter
	@Column(name="DETAIN_DATE")
	private Date detainDate;
	@Getter
	@Setter
	@Column(name="ATF_YEAR_COUNT")
	private BigDecimal atfYearCount;
	@Getter
	@Setter
	@Column(name="PROTOCOL_DATE")
	private Date protocolDate;
	@Getter
	@Setter
	@Column(name="REGISTRAR_ID")
	private Long registrar;
	@Getter
	@Setter
	@Column(name="CERT_ISSUE_DATE")
	private Date certIssueDate;
	@Getter
	@Setter
	@Column(name = "PROV_REG_DATE")
	@Temporal(TemporalType.DATE)
	private Date provRegDate;


	@Getter
	@Setter
	@OneToOne(fetch=FetchType.EAGER, optional=true)
	@JoinColumn(name="APPL_NO", insertable=false, updatable=false, nullable=true)
	private Representative rp;

	public String getRepresentativeName() {
		return rp != null ? rp.getName() : null;
	}

	@Override
	public String getId() {
		return getApplNo();
	}

	@Override
	public RegMaster clone() {
		try {
			RegMaster clone = (RegMaster) super.clone();
			clone.setCreatedBy(null);
			clone.setCreatedDate(null);
			clone.setUpdatedBy(null);
			clone.setUpdatedDate(null);
			clone.setVersion(null);
			clone.setCountry(null);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeErrorException(new Error(e));
		}
	}

}
