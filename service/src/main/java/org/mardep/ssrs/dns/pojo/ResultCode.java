package org.mardep.ssrs.dns.pojo;

public class ResultCode {
	
//	Common
	public final static ResultCode RC_00000= new ResultCode("00000", "Success");
	public final static ResultCode RC_98000= new ResultCode("98000", "Invalid data format");
	public final static ResultCode RC_99000= new ResultCode("99000", "Unknown error");
	
//	Outbound
//	4.1.1	Upload, Update or Cancel Demand Note Record
	public final static ResultCode RC_00001= new ResultCode("00001", "Fail: Demand Note record is not up-to-date");
	public final static ResultCode RC_90001= new ResultCode("90001", "Bill type is empty or is invalid");
	public final static ResultCode RC_90003= new ResultCode("90003", "Demand Note Number is empty or invalid");
	public final static ResultCode RC_90004= new ResultCode("90004", "Last Update Datetime is empty or invalid date");
	public final static ResultCode RC_90005= new ResultCode("90005", "Issue Date is empty or invalid date");
	public final static ResultCode RC_90007= new ResultCode("90007", "Amount payable is empty or invalid");
	public final static ResultCode RC_90008= new ResultCode("90008", "Name of payer is empty (skip mandatory check for LSLVS)");
	public final static ResultCode RC_90009= new ResultCode("90009", "Issuing Office ID is empty or invalid");
	public final static ResultCode RC_90010= new ResultCode("90010", "Issuing user code is empty or invalid");
	public final static ResultCode RC_90011= new ResultCode("90011", "DN Status is empty or invalid status code");
	public final static ResultCode RC_90012= new ResultCode("90012", "Payment Status is empty or invalid status code");
	public final static ResultCode RC_90013= new ResultCode("90013", "Auto-payment status (isAutopay) is empty or invalid code");
	public final static ResultCode RC_90014= new ResultCode("90014", "Is Remark is empty or invalid code");
	public final static ResultCode RC_90015= new ResultCode("90015", "Amount of item is empty for payment item");
	public final static ResultCode RC_90016= new ResultCode("90016", "Unit Price in particular is empty for payment item");
	public final static ResultCode RC_90017= new ResultCode("90017", "Unit in particular is empty for payment item");
	public final static ResultCode RC_90018= new ResultCode("90018", "Autopay Request to Redev DNS is empty or invalid");
	public final static ResultCode RC_96000= new ResultCode("96000", "Bank account number of payer for autopay is empty but Autopay Request is enable");
	public final static ResultCode RC_96001= new ResultCode("96001", "Bank account debtor reference of payer is empty but Autopay Request is enable");
	public final static ResultCode RC_96002= new ResultCode("96002", "Bank account debtor name of payer is empty but Autopay Request is enable");
	public final static ResultCode RC_96003= new ResultCode("96003", "Bank account limit for autopay is empty but Autopay Request is enable");

//	4.1.2	Upload or Cancel Demand Note Payment
	public final static ResultCode RC_00101= new ResultCode("00101", "Success: But no Demand Note Number found for the receipt data");
	public final static ResultCode RC_90101= new ResultCode("90101", "Receipt Number is empty or too long");
	public final static ResultCode RC_90102= new ResultCode("90102", "Receipt Date is empty or invalid date");
	public final static ResultCode RC_90103= new ResultCode("90103", "Demand Note Number is empty or invalid");
	public final static ResultCode RC_90104= new ResultCode("90104", "Bill Type is empty or invalid");
	public final static ResultCode RC_90105= new ResultCode("90105", "Machine ID is empty or too long");
	public final static ResultCode RC_90106= new ResultCode("90106", "Payment Type  is empty or too long");
	public final static ResultCode RC_90107= new ResultCode("90107", "Payment Amount is empty or too long");
	public final static ResultCode RC_90109= new ResultCode("90109", "Action is empty or invalid");
	public final static ResultCode RC_90110= new ResultCode("90110", "Receipt Amount is empty or too long");

//	4.1.3	Retrieve Demand Note Settlement
	public final static ResultCode RC_00201= new ResultCode("00201", "Fail: Demand note number does not exist");
	public final static ResultCode RC_90202= new ResultCode("90202", "Demand note number is empty or invalid");
	public final static ResultCode RC_90203= new ResultCode("90203", "Bill Type  is empty or invalid");

//	4.1.4	Refund Request
	public final static ResultCode RC_00301= new ResultCode("00301", "Fail: Demand note number do not exist");
	public final static ResultCode RC_00302= new ResultCode("00302", "Fail: Refund request for Demand note item cannot be matched");
	public final static ResultCode RC_00303= new ResultCode("00303", "Fail: Refund Application ID is duplicated in Redev DNS");
	public final static ResultCode RC_00304= new ResultCode("00304", "Fail: The Demand note not yet pay");
	public final static ResultCode RC_90302= new ResultCode("90302", "Demand note number is empty or invalid");
	public final static ResultCode RC_90303= new ResultCode("90303", "Bill Type is empty or invalid");
	public final static ResultCode RC_90304= new ResultCode("90304", "User code is empty");
	public final static ResultCode RC_90305= new ResultCode("90305", "Office id is empty");
	public final static ResultCode RC_90306= new ResultCode("90306", "Amount of refund is empty or invalid");

	
//	Inbound
//	5.1.1	Update Demand Note Record Status
	public final static ResultCode RC_00401= new ResultCode("00401", "Fail: Demand note number do not exist");
	public final static ResultCode RC_90402= new ResultCode("90402", "Demand note number is empty or invalid");
	public final static ResultCode RC_90403= new ResultCode("90403", "Bill Type is empty or invalid");
	public final static ResultCode RC_90404= new ResultCode("90404", "User code is empty");
	public final static ResultCode RC_90405= new ResultCode("90405", "Office id is empty");
	
//	5.1.2	Update Refund Request Status
	public final static ResultCode RC_00501= new ResultCode("00501", "Fail: Demand note number do not exist");
	public final static ResultCode RC_90502= new ResultCode("90502", "Demand note number is empty or invalid");
	public final static ResultCode RC_90503= new ResultCode("90503", "Bill Type is empty or invalid");
	public final static ResultCode RC_90504= new ResultCode("90504", "User code is empty");
	public final static ResultCode RC_90505= new ResultCode("90505", "Office id is empty");
	public final static ResultCode RC_90506= new ResultCode("90506", "Receipt number is empty or invalid");

//	5.1.3	Upload or Cancel Demand Note Payment To Other Systems
	public final static ResultCode RC_00601= new ResultCode("00601", "Success: But no Demand Note Number found for the receipt data");
	public final static ResultCode RC_00602= new ResultCode("00602", "Fail: Duplicated Receipt Number found when creation of receipt");
	public final static ResultCode RC_90601= new ResultCode("90601", "Receipt Number is empty or too long");
	public final static ResultCode RC_90602= new ResultCode("90602", "Receipt Date is empty or invalid date");
	public final static ResultCode RC_90603= new ResultCode("90603", "Demand Note Number is empty or too long");
	public final static ResultCode RC_90604= new ResultCode("90604", "Machine ID is empty or too long");
	public final static ResultCode RC_90605= new ResultCode("90605", "Payment Type is empty or too long");
	public final static ResultCode RC_90606= new ResultCode("90606", "Receipt Amount is empty or too long");
	public final static ResultCode RC_90607= new ResultCode("90607", "Action is empty or invalid");
	public final static ResultCode RC_90608= new ResultCode("90608", "Success: But Demand Note is already cancelled / written off");
	
	private String code;
	private String description;
	public ResultCode(String code, String description){
		this.code = code;
		this.description = description;
	}
	
	public String getCode() {
		return code;
	}
	public String getDescription() {
		return description;
	}
}
