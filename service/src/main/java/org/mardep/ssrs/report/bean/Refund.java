package org.mardep.ssrs.report.bean;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class Refund extends ReceiptCollected01{

	private BigDecimal refundAmount;
	
}
