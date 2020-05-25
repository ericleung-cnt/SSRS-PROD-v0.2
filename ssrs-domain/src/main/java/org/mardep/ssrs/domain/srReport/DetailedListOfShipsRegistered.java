package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

//@Entity
public class DetailedListOfShipsRegistered {

	@Getter
	@Setter
	private String shipNameEng;
	
	@Getter
	@Setter
	private String surveyShipType;
	
	@Getter
	@Setter
	private String officialNumber;
	
	@Getter
	@Setter
	private String applNo;
	
	@Getter
	@Setter
	private String callSign;
	
	@Getter
	@Setter
	private BigDecimal grossTon;
	
	@Getter
	@Setter
	private BigDecimal netTon;
	
	@Getter
	@Setter
	private Long txId;

	@Getter
	@Setter
	private String owner;
	
	@Getter
	@Setter
	private String ownerAddress1;
	
	@Getter
	@Setter
	private String ownerAddress2;
	
	@Getter
	@Setter
	private String ownerAddress3;
	
}
