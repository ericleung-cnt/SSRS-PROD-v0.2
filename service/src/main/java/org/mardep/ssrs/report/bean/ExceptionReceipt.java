package org.mardep.ssrs.report.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ExceptionReceipt extends ReceiptCollected01{

	public String remarks;
	
}
