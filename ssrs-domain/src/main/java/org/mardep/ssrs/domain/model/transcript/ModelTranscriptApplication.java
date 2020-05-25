package org.mardep.ssrs.domain.model.transcript;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class ModelTranscriptApplication {
	
	@Getter
	@Setter
	private Integer id;
	
	@Getter
	@Setter
	private String applNo;
	
	@Getter
	@Setter
	private String imoNo;
	
	@Getter
	@Setter
	private String shipNameEng;
	
	@Getter
	@Setter
	private String shipNameChi;
	
	@Getter
	@Setter
	private String officialNo;
	
	@Getter
	@Setter
	private String officeCode;
	
	@Getter
	@Setter
	private Integer officeId;
	
	@Getter
	@Setter
	private Date reportDate;
	
	@Getter
	@Setter
	private String reportGenBy;
	
	@Getter
	@Setter
	private Boolean paymentRequired;
	
	@Getter
	@Setter
	private Boolean delayPaymentRequired;
	
	@Getter
	@Setter
	private Long registrar;
	
	@Getter
	@Setter
	private Boolean certified;
	
	@Getter
	@Setter
	private String noPaymentReason;
	
	@Getter
	@Setter
	private String delayPaymentReason;
	
	@Getter
	@Setter
	private String issueOffice;	// from cert issue log
	
	@Getter
	@Setter
	private Integer issueOfficeId;
	
	@Getter
	@Setter
	private Date issueDate;	// from cert issue log
	
	@Getter
	@Setter
	private String issueBy;	// from cert issue log

	@Getter
	@Setter
	private String demandNoteNo;	// from cert demand note log
	
	@Getter
	@Setter
	private String demandNotePaymentStatus;	// from demand note header
}
