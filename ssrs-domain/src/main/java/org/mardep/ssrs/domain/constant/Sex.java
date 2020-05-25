package org.mardep.ssrs.domain.constant;

public enum Sex {

	M("Male"),
	F("Female"),
	
	;
	
	private String name;
	Sex(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
}
