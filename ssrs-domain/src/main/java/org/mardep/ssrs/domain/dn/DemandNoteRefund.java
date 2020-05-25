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

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



@Entity
@Table(name="DEMAND_NOTE_REFUNDS")
@NoArgsConstructor
@ToString(of={"refundId"})
public class DemandNoteRefund extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;

	/**
	 * add as PK
	 *
	 */
	@Id
	@Getter
	@Setter
	@Column(name = "REFUND_ID", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long refundId;


	@Getter
	@Setter
	@Column(name = "DN_DEMAND_NOTE_NO", length=15)
	private String demandNoteNo;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="DN_DEMAND_NOTE_NO", referencedColumnName="DEMAND_NOTE_NO", updatable=false, insertable=false, foreignKey=@ForeignKey(name="DNR_DN_FK"))
	private DemandNoteHeader demandNoteHeader;

	@Getter
	@Setter
	@Column(name = "AMOUNT", nullable=false, precision=10, scale=2)
	private BigDecimal refundAmount;

	@Getter
	@Setter
	@Column(name = "REMARKS", nullable=false)
	private String remarks;

	@Getter
	@Setter
	@Column(name = "DNS_RESULT")
	private String dnsResult;

	@Getter
	@Setter
	@Column(name = "DNS_DESCRIPTION")
	private String dnsDescription;

	@Getter
	@Setter
	@Column(name = "STATUS")
	private String status;
	@Getter
	@Setter
	@Column(name = "VOUCHER_NO")
	private String voucherNo;
	@Getter
	@Setter
	@Column(name = "REPAY_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date repayDate;
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "VOUCHER_DATE")
	private Date voucherDate;
	@Getter
	@Setter
	@Column(name = "USER_CODE")
	private String userCode;

	@Getter
	@Setter
	@Column(name = "REFUND_STATUS", length=15)
	private String refundStatus;

	@Getter
	@Setter
	@Column(name = "DNS_REMARK", length=255)
	private String dnsRemark;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REFUND_STATUS_UPD_DATE")
	private Date refundStatusDnsUpdDate;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RECOMMENDED_DATE")
	private Date recommendedDate;

	@Override
	public Long getId() {
		return refundId;
	}

}
