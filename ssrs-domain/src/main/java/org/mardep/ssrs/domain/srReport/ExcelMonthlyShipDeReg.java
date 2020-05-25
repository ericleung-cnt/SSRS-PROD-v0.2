package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class ExcelMonthlyShipDeReg {
	
	@Getter
	@Setter
	private Date deRegTime;

	@Getter
	@Setter
	private String regName;

	@Getter
	@Setter
	private BigDecimal grossTon;

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
	private String applNoSuff;
}
