package org.mardep.ssrs.domain.dn;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.FeeCode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Demand note item may be created without header for Ship Registration.
 * Item is created during workflow where the header will be created later.
 * @author ssrsc
 *
 */
@Entity
@Table(name="DEMAND_NOTE_ITEMS")
@NoArgsConstructor
@ToString(of={"itemId"})
public class DemandNoteItem extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;

	/**
	 * add as PK
	 *
	 */
	@Id
	@Getter
	@Setter
	@Column(name = "ITEM_ID", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long itemId;

	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", length=9)
	private String applNo;

	@Getter
	@Setter
	@Column(name = "ITEM_NO")
	private Long itemNo;

	@Getter
	@Setter
	@Column(name = "DN_DEMAND_NOTE_NO")
	private String dnDemandNoteNo;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="DN_DEMAND_NOTE_NO", referencedColumnName="DEMAND_NOTE_NO", updatable=false, insertable=false, foreignKey=@ForeignKey(name="DNI_DN_FK"))
	private DemandNoteHeader demandNoteHeader;

	@Getter
	@Setter
	@Column(name = "CHARGED_UNITS", nullable=false)
	private Integer chargedUnits;

	@Getter
	@Setter
	@Column(name = "AMOUNT", nullable=false, precision=10, scale=2)
	private BigDecimal amount;

	@Getter
	@Setter
	@Column(name = "CHG_INDICATOR", nullable=false, length=1)
	private String chgIndicator; // TODO boolean

	@Getter
	@Setter
	@Column(name = "ADHOC_DEMAND_NOTE_TEXT", length=2000)
	private String adhocDemandNoteText; // TODO length

	@Getter
	@Setter
	@Column(name = "USER_ID", length=50, nullable=false)
	private String userId;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "GENERATION_TIME", nullable=false)
	private Date generationTime;

	@Getter
	@Setter
	@Column(name = "FC_FEE_CODE", length=50)
	private String fcFeeCode;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FC_FEE_CODE", referencedColumnName="FEE_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="DNI_FC_FK"))
	private FeeCode feeCode;

	@Getter
	@Setter
	@Column(name="ACTIVE")
	private Boolean active = true;

	@Getter
	@Setter
	@Column(name="DELETE_REASON")
	private String deleteReson;

	@Getter
	@Setter
	@Column(name="ADJUST_AMT_REASON")
	private String adjustAmtReason;

	@Transient
	@Getter
	@Setter
	private BigDecimal unitPrice;
	
	@Override
	public Long getId() {
		return itemId;
	}

}
