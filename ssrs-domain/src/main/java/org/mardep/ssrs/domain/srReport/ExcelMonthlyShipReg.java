package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class ExcelMonthlyShipReg {
	
	@Getter
	@Setter
	private String regName;

	@Getter
	@Setter
	private Date regDate;

	@Getter
	@Setter
	private String ownerName;

	@Getter
	@Setter
	private String rpName;

	@Getter
	@Setter
	private String otOperTypeCode;
	
	@Getter
	@Setter
	private BigDecimal grossTon;
}
