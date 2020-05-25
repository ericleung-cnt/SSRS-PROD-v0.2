package org.mardep.ssrs.report.bean;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DemandNoteItemBean {

	private String feeDescription;
	private Integer chargedUnit;
	private BigDecimal feePrice;
	private BigDecimal feeAmount;
}
