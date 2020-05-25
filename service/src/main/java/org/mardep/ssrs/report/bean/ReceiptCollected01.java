package org.mardep.ssrs.report.bean;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReceiptCollected01 {

	protected String demandNoteNo;
	protected String issueDate;
	protected String dueDate;
	protected String demandNoteStatus;
	protected String receiptStatus;
	protected String receiptDate;
	protected String receiptNo;
	protected String billName;
	protected BigDecimal amount;
	protected BigDecimal receiptAmount;
	protected String isReceiptCancelled;
	protected String itemCode;
	protected String feeCode;

	protected String userName;
	protected String remarks;
	protected String updateTime;


	protected String appNo;
	protected BigDecimal refundAmount;
	protected String refundDate;
	protected String paymentType;

	protected String title;
	protected BigDecimal count = BigDecimal.ZERO;
	protected BigDecimal cancelledAmount = BigDecimal.ZERO;
	protected BigDecimal receivedAmount = BigDecimal.ZERO;

}
