package org.mardep.ssrs.domain.sr;

import java.util.Date;

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
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.TransactionCode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="TRANSACTIONS")
//@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "transactionTime", "code"})
public class Transaction extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;

	public static final String CODE_DISCHARGE_MORTGAGE = "35";

	public static final String CODE_CANCEL_MORTGAGE = "36";

	public static final String CODE_REG_MORTGAGE = "33";

	public static final String CODE_TRANSFER_MORTGAGE = "34";
	public static final String CODE_DE_REG = "43";

	public static final String CODE_MORTGAGE_DETAIL = "32";

	public static final String CODE_MORTGAGEES_CHANGE = "31";

	public static final String CODE_CHG_OWNER_OTHERS = "19";

	public static final String CODE_CHG_OWNER_NAME = "17";

	public static final String CODE_CHG_OWNER_ADDR = "18";

	public static final String CODE_CHG_RP_OTHERS = "27";

	public static final String CODE_CHG_RP_NAME = "25";

	public static final String CODE_CHG_RP_ADDR = "26";

	public static final String CODE_BUILDER_DETAILS = "22";

	public static final String CODE_TRANSMISSION_OWNERSHIP = "15";

	public static final String CODE_REGISTRATION = "71";

	public static final String CODE_CHG_SHIP_PARTICULARS = "50";

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "AT_SER_NUM", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	@Getter
	@Setter
	@Column(name = "TXN_TIME", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date transactionTime;

	@Getter
	@Setter
	@Column(name = "TC_TXN_CODE", nullable=false, length=2)
	private String code;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false)
    }, foreignKey=@ForeignKey(name="OWR_RMR_FK")
	)
	private RegMaster regMaster;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="TC_TXN_CODE", referencedColumnName="TXN_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="TRA_TC_FK"))
	private TransactionCode transactionCode;


	@Getter
	@Setter
	@Column(name = "USER_ID", length=8)
	private String userId;

	@Getter
	@Setter
	@Column(name = "DATE_CHANGE")
	@Temporal(TemporalType.DATE)
	private Date dateChange;

	@Getter
	@Setter
	@Column(name = "HOUR_CHANGE", length=15)
	private String hourChange;

	@Getter
	@Setter
	@Column(name="TXN_NATURE_DETAILS", length=720)
	private String details;

	/**
	 * mortgage transaction can be handled by "OWNER", "RP" or "AGENT"
	 * ALTER table transactions add HANDLED_BY nvarchar(20)
	 */
	@Getter
	@Setter
	@Column(name="HANDLED_BY", length=10)
	private String handledBy;

	/**
	 * mortgage transaction agent name if it is handled by "AGENT"
	 */
	@Getter
	@Setter
	@Column(name="HANDLING_AGENT", length=80)
	private String handlingAgent;

	/**
	 * ALTER table transactions add PRIORITY_CODE nvarchar(2)
	 */
	@Getter
	@Setter
	@Column(name="PRIORITY_CODE", length=2)
	private String priorityCode;

	@Override
	public Long getId() {
		return id;
	}

}
