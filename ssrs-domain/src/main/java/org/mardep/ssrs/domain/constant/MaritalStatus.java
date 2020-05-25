package org.mardep.ssrs.domain.constant;

public enum MaritalStatus {

	SINGLE("Single"),
	MARRIED("Married"),
	WIDOWED("Widowed"),
	DIVORCED("Divorced"),
	
	;
	
	private String name;
	MaritalStatus(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
}
