package org.mardep.ssrs.domain.user;

public enum LogStatus {
	
	S("Success"),
	F("Fail"),
	D("Unknown"),
	L("Unknown"),
	
	;
	private String description;
	LogStatus(String description){
		this.description = description;
	}
 
	public String getDescription() {
		return description;
	}

}

