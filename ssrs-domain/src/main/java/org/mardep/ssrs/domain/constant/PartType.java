package org.mardep.ssrs.domain.constant;

public enum PartType{
	I("1", "Part I"),
	II("2", "Part II")
	;
	
	String value;
	String displayName;
	
	PartType(String value, String displayName){
		this.value = value;
		this.displayName = displayName;
	}

	public String getValue() {
		return value;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public static PartType getByValue(String value){
		for(PartType pt:PartType.values()){
			if(pt.getValue().equals(value)){
				return pt;
			}
		}
		return null;
	}
	
}
