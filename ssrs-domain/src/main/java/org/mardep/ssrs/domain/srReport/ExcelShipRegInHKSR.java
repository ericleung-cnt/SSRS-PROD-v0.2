package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

public class ExcelShipRegInHKSR {
	
	@Getter
	@Setter
	private String regName;
	
	@Getter
	@Setter
	private String officialNo;

	@Getter
	@Setter
	private String callSign;

	@Getter
	@Setter
	private String imoNo;

	@Getter
	@Setter
	private String surveyShipType;
	
	@Getter
	@Setter
	private BigDecimal grossTon;
	
	@Getter
	@Setter
	private BigDecimal netTon;
}
