package org.mardep.ssrs.domain.constant;

public enum DemandNoteType {

	REGULAR("Regular"),
	AD_HOC("AD_HOC"),
	
	;
	
	private String name;
	DemandNoteType(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
}
