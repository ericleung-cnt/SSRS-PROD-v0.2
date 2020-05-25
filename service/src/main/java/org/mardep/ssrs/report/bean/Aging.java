package org.mardep.ssrs.report.bean;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class Aging extends ReceiptCollected01{

	private BigDecimal day21;
	private BigDecimal day41;
	private BigDecimal day90;
	private BigDecimal dayOver90;
	private String firstRemDate;
	private String secondRemDate;
	
}
