package org.mardep.ssrs.domain.sr;

public enum EtoCorValidState {
	VALID("Y"),
	INVALID("N"),
	
	;
	
	private String code;
	
	EtoCorValidState(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

}
