package org.mardep.ssrs.domain.sr;

public enum EtoCorActiveState {
	ACTIVE("Y"),
	INACTIVE("N"),
	
	;
	
	private String code;
	
	EtoCorActiveState(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
}
