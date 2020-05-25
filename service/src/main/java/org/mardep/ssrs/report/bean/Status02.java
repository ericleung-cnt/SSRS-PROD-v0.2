package org.mardep.ssrs.report.bean;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Status02 {
	
	public String status;
	public BigDecimal amount = BigDecimal.ZERO;
	public Integer number = 0;

	public Status02(String status){
		this.status = status;
	}
	
	public void addAmount(BigDecimal amount){
		this.amount = this.amount.add(amount);
		number++;
	}
}
