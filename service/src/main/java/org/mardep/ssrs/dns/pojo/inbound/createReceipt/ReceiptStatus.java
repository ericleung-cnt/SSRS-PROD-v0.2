package org.mardep.ssrs.dns.pojo.inbound.createReceipt;

import org.mardep.ssrs.dns.pojo.BaseRequest;
import org.mardep.ssrs.dns.pojo.common.CreateReceiptAction;
import org.mardep.ssrs.dns.pojo.common.ReceiptInfo;

public class ReceiptStatus extends BaseRequest {

	private CreateReceiptAction action;
	private int payStatus;
	private ReceiptInfo receipt;
	
	public CreateReceiptAction getAction() {
		return action;
	}
	public void setAction(CreateReceiptAction action) {
		this.action = action;
	}
	
	public int getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(int payStatus) {
		this.payStatus = payStatus;
	}
	public ReceiptInfo getReceipt() {
		return receipt;
	}
	public void setReceipt(ReceiptInfo receipt) {
		this.receipt = receipt;
	}
	
}
