package org.mardep.ssrs.domain.constant;

public enum EyeTestResult {

	PASS("Pass"),
	FAIL("Fail"),
	NIL("Nil"),
	
	;
	
	private String name;
	EyeTestResult(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
}
