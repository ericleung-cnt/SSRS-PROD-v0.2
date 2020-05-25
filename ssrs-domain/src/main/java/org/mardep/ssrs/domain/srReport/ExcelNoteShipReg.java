package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class ExcelNoteShipReg {
	
	@Getter
	@Setter
	private Date regDate;

	@Getter
	@Setter
	private BigDecimal grossTonSum;

	@Getter
	@Setter
	private String regName;

	@Getter
	@Setter
	private String ssStShipTypeCode;
}
