package org.mardep.ssrs.domain.dn;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

public class AdjustAtcAmt {
	
	@Getter
	@Setter
	private Long itemId;
	
	@Getter
	@Setter
	private String applNo;

	@Getter
	@Setter
	private BigDecimal Amt100;

	@Getter
	@Setter
	private BigDecimal Amt50;

	@Getter
	@Setter
	private BigDecimal currentAmt;

	@Getter
	@Setter
	private BigDecimal adjustAmt;

	@Getter
	@Setter
	private String adhocText;

	@Getter
	@Setter
	private String adjustAmtReason;
}
