package org.mardep.ssrs.constant;

public enum UserLoginResult {

	SUCCESSFUL(0, "Login successful!"),
	USER_NOT_EXIST(-1, "User not exist!"),
	USER_DISABLED(-2, "User disabled!"),
	PASSWORD_INCORRECT(-3, "Password incorrect!"), 
	LDAP_AUTH_FAILED(-4, "LDAP Authentication failed!"),
	LDAP_CHANGE_PASSWORD(-5, "Password expired! Please go to DP and change password first."),
	
	;
	
	private int id;
	private String message;
	
	UserLoginResult(int id, String message){
		this.id = id;
		this.message = message;
	}

	public int getId() {
		return id;
	}
	public String getMessage() {
		return message;
	}
}
