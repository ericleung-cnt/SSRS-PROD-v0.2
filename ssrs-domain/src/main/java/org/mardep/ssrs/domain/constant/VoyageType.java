package org.mardep.ssrs.domain.constant;

@Deprecated
public enum VoyageType {

	S("Sea-Going"),
	R("River-Trade"),

	;

	private String name;
	VoyageType(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}

}
