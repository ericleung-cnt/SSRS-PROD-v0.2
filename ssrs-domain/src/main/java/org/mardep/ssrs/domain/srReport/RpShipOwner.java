package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

public class RpShipOwner {

	@Getter
	@Setter
	private String rpName;
	
	@Getter
	@Setter
	private String rpAddr1;
	
	@Getter
	@Setter
	private String rpAddr2;
	
	@Getter
	@Setter
	private String rpAddr3;
	
	@Getter
	@Setter
	private String ownerName;
	
	@Getter
	@Setter
	private String ownerAddr1;
	
	@Getter
	@Setter
	private String ownerAddr2;
	
	@Getter
	@Setter
	private String ownerAddr3;
	
	@Getter
	@Setter
	private String shipNameEng;
	
	@Getter
	@Setter
	private String shipNameChi;
	
	@Getter
	@Setter
	private BigDecimal grossTon;
	
	@Getter
	@Setter
	private BigDecimal netTon;
	
	@Getter
	@Setter
	private String officialNo;
	
	@Getter
	@Setter
	private String callSign;
	
	@Getter
	@Setter
	private String surveyShipType;
	
	@Getter
	@Setter
	private String tel;
	
	@Getter
	@Setter
	private String fax;
	
	@Getter
	@Setter
	private String telex;
	
}
