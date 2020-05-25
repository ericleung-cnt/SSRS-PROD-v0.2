package org.mardep.ssrs.domain.constant;

public enum DemandNoteRefundStatus {
	RECOMMENDED("Recommended"),
	SUBMITTED("Submitted"),
	APPROVED("Approved"),
	REJECTED("Rejected");
	
	private String name;
	DemandNoteRefundStatus(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
