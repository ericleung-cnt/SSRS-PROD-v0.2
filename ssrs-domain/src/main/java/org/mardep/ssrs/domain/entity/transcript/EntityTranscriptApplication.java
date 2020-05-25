package org.mardep.ssrs.domain.entity.transcript;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="TRANSCRIPT_APPLICATION")
public class EntityTranscriptApplication extends AbstractPersistentEntity<Integer>{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	//@Setter
	@Column(name = "APPLICATION_ID", nullable=false)
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", length=9)
	private String applNo;
	
	@Getter
	@Setter
	@Column(name = "IMO_NO", length=9)
	private String imoNO;
	
	@Getter
	@Setter
	@Column(name = "SHIP_NAME_ENG", length=70)
	private String shipNameEng;
	
	@Getter
	@Setter
	@Column(name = "SHIP_NAME_CHI", length=70)
	private String shipNameChi;
	
	@Getter
	@Setter
	@Column(name = "OFFICIAL_NO", length=9)
	private String officialNo;
	
	@Getter
	@Setter
	@Column(name = "OFFICE_CODE", length=10, nullable=true)
	private String officeCode;
	
	@Getter
	@Setter
	@Column(name = "OFFICE_ID")
	private Integer officeId;
	
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REPORT_DATE")
	private Date reportDate;
	
//	@Getter
//	@Setter
//	@OneToMany(fetch=FetchType.EAGER)
//	@JoinColumn(name="APPLICATION_ID", referencedColumnName="APPLICATION_ID", updatable=false, insertable=false)
//	private Set<FuncEntitle> funcEntitles;
	
	@Getter
	@Setter
	@Column(name = "REPORT_GEN_BY", length=50)
	private String reportGenBy;
	
	@Getter
	@Setter
	@Column(name = "PAYMENT_REQUIRED_IND")
	private Boolean paymentRequired;
	
	@Getter
	@Setter
	@Column(name = "DELAY_PAYMENT_REQUIRED_IND")
	private Boolean delayPaymentRequired;
	
	@Getter
	@Setter
	@Column(name = "REGISTRAR")
	private Long registrar;
	
	@Getter
	@Setter
	@Column(name = "IS_CERTIFIED_IND")
	private Boolean certified;
	
	@Getter
	@Setter
	@Column(name = "NO_PAYMENT_REASON", length=200)
	private String noPaymentReason;
	
	@Getter
	@Setter
	@Column(name = "DELAY_PAYMENT_REASON", length=200)
	private String delayPaymentReason;
	
//	@Getter
//	@Setter
//	@Column(name = "PAYMENT_STATUS", length=30)
//	private String paymentStatus;
	
//	@Getter
//	@Setter
//	@Column(name = "IS_PRINT_MORTGAGE_IND")
//	private Boolean printMortgage;
	
//	@Getter
//	@Setter
//	@Column(name = "DEMAND_NOTE_NO", length=50)
//	private String demandNoteNo;
	
//	@Getter
//	@Setter
//	@Column(name = "ISSUE_OFFICE", length=30)
//	private String issueOffice;
//	
//	@Getter
//	@Setter
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name = "ISSUE_DATE")
//	private Date issueDate;
//
//	public String getIssueDateStr() {
//		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		return sdf.format(this.issueDate);
//	}
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return id;
	}

}
