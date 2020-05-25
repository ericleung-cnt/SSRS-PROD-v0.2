package org.mardep.ssrs.domain.dn;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name="DEMAND_NOTE_HEADERS")
@NoArgsConstructor
@Audited
@ToString(of={"demandNoteNo"})
public class DemandNoteHeader extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	public static final String STATUS_ISSUED = "3";
	public static final String STATUS_WRITTEN_OFF = "11";
	public static final String STATUS_CANCELLED = "12";
	public static final String STATUS_REFUNDED = "16";
	public static final String PAYMENT_STATUS_OUTSTANDING = "0";
	public static final String PAYMENT_STATUS_PAID = "1";
	public static final String PAYMENT_STATUS_PARTIAL = "2";
	public static final String PAYMENT_STATUS_OVERPAID = "3";
	public static final String PAYMENT_STATUS_AUTOPAY_ARRANGED = "4";



	/**
	 * it is PK before, alter to normal property, and nullable
	 */
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", length=9)
	private String applNo;

	/**
	 * it is PK before, alter to normal property, and nullable
	 */
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO_SUF", length=1)
	private String applNoSuf;

	/**
	 * it is Long before, change to String to match Marine DemandNote format.
	 *
	 */
	@Id
	@Getter
	@Setter
	@Column(name = "DEMAND_NOTE_NO", nullable=false, length=15)
	private String demandNoteNo;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "GENERATION_TIME")
	private Date generationTime;

	@Getter
	@Setter
	@Column(name = "BILL_NAME", length=160, nullable=false)
	private String billName;

//	@Getter
//	@Setter
//	@Column(name = "BILL_NAME2", length=40)
//	private String billName2;

	@Getter
	@Setter
	@Column(name = "CO_NAME", length=160)
	private String coName;

//	@Getter
//	@Setter
//	@Column(name = "CO_NAME2", length=40)
//	private String coName2;

	@Getter
	@Setter
	@Column(name = "ADDRESS1", length=80)
	private String address1;

	@Getter
	@Setter
	@Column(name = "ADDRESS2", length=80)
	private String address2;

	@Getter
	@Setter
	@Column(name = "ADDRESS3", length=80)
	private String address3;

	@Getter
	@Setter
	@Column(name = "EMAIL", length=50)
	private String email;

	@Getter
	@Setter
	@Column(name = "TEL", length=18)
	private String tel;
	
	@Getter
	@Setter
	@Column(name = "FAX", length=18)
	private String fax;
	
	@Getter
	@Setter
	@Column(name = "AMOUNT", nullable=false, precision=10, scale=2)
	private BigDecimal amount;

	@Getter
	@Setter
	@Column(name = "ADJUST_REASON", length=40)
	private String adjustReason;

	@Getter
	@Setter
	@Column(name = "ADJUST_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date adjustTime;

	@Getter
	@Setter
	@Column(name = "RCP_AMOUNT", precision=10, scale=2)
	private BigDecimal rcpAmount;

	@Getter
	@Setter
	@Column(name = "ACCT_AMOUNT", precision=10, scale=2)
	private BigDecimal acctAmount;

	@Getter
	@Setter
	@Column(name = "CW_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date cwTime;


	@Getter
	@Setter
	@Column(name = "CW_STATUS", length=1)
	private String cwStatus; // TODO enum ?

	@Getter
	@Setter
	@Column(name = "CW_REMARK", length=180)
	private String cwRemark;

	@Getter
	@Setter
	@Column(name = "CW_BY", length=10)
	private String cwBy;

	@Getter
	@Setter
	@Column(name = "DUE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dueDate;
	/**
	 * add
	 *
	 */
	@Getter
	@Setter
	@Column(name = "AMOUNT_PAID", nullable=true, precision=10, scale=2)
	private BigDecimal amountPaid;

//	@Getter
//	@Setter
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="demandNoteHeader")
//	private Set<DemandNoteItem> demandNoteItem;
//
//	@Getter
//	@Setter
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="demandNoteHeader")
//	private Set<DemandNoteReceipt> demandNoteReceipt;
//
//	@Getter
//	@Setter
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="demandNoteHeader")
//	private Set<DemandNoteRefund> demandNoteRefund;

	@Getter
	@Setter
	@Transient
	private List<DemandNoteItem> demandNoteItems;

	@Override
	public String getId() {
		return demandNoteNo;
	}

	@Transient
	@Setter
	@Getter
	private byte[] pdf;

	@Setter
	@Getter
	@Column(name = "PAYMENT_STATUS", length=50)
	private String paymentStatus = PAYMENT_STATUS_OUTSTANDING;

	@Setter
	@Getter
	@Column(name = "DEMAND_NOTE_STATUS", length=50)
	private String status = STATUS_ISSUED;

	@Setter
	@Getter
	@Column(name = "FIRST_REMINDER_FLAG")
	private String firstReminderFlag;

	@Setter
	@Getter
	@Column(name = "FIRST_REMINDER_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date firstReminderDate;

	@Setter
	@Getter
	@Column(name = "SECOND_REMINDER_FLAG")
	private String secondReminderFlag;

	@Setter
	@Getter
	@Column(name = "SECOND_REMINDER_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date secondReminderDate;

	@Setter
	@Getter
	@Column(name = "SHIP_NAME_ENG")
	private String shipNameEng;

	@Setter
	@Getter
	@Column(name = "SHIP_NAME_CHI")
	private String shipNameChi;

	@Setter
	@Getter
	@Column(name = "GROSS_TON")
	private BigDecimal grossTon;

	@Setter
	@Getter
	@Column(name = "NET_TON")
	private BigDecimal netTon;

	@Setter
	@Getter
	@Column(name = "EBS_FLAG")
	private String ebsFlag;

	public String getStatusStr(){
		if(STATUS_ISSUED.equals(getStatus())){
			return  "Issued";
		}else if(STATUS_WRITTEN_OFF.equals(getStatus())){
			return  "Written Off";
		}else if(STATUS_CANCELLED.equals(getStatus())){
			return  "Cancelled";
		}else if(STATUS_REFUNDED.equals(getStatus())){
			return  "Refunded";
		}
		return null;
	}
	public String getPaymentStatusStr(){
		if(PAYMENT_STATUS_OUTSTANDING.equals(getPaymentStatus())){
			return  "Outstanding";
		}else if(PAYMENT_STATUS_PAID.equals(getPaymentStatus())){
			return  "Paid (Full)";
		}else if(PAYMENT_STATUS_PARTIAL.equals(getPaymentStatus())){
			return  "Outstanding (Partial)";
		}else if(PAYMENT_STATUS_OVERPAID.equals(getPaymentStatus())){
			return  "Paid (Overpaid)";
		}
		return null;
	}
	public String getIssueDateStr() {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(this.generationTime);
	}
	public String getDueDateStr() {
		if(this.dueDate==null) return null;
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(this.dueDate);
	}
	public String getFirstReminderDateStr() {
		if (this.firstReminderDate!=null) {
			DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(this.firstReminderDate);
		} else {
			return "";
		}
	}
	public String getSecondReminderDateStr() {
		if (this.secondReminderDate!=null) {
			DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(this.secondReminderDate);
		} else {
			return "";
		}
	}


}
