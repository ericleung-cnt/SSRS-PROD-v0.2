package org.mardep.ssrs.report.bean;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReceiptCollected02 implements Comparable<ReceiptCollected02>{

	public String itemCode;
	public String feeCode;
	public String description;
	public BigDecimal price;
	public Integer chargedUnit;
	public BigDecimal amount;
	@Override
	public int compareTo(ReceiptCollected02 o) {
		return itemCode.compareTo(o.getItemCode());
	}

}
