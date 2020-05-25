package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class ExcelNoteShipDeReg {
	
	@Getter
	@Setter
	private String ssStShipTypeCode;

	@Getter
	@Setter
	private String regName;

	@Getter
	@Setter
	private Date deRegTime;

	@Getter
	@Setter
	private BigDecimal grossTon;
}
