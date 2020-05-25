package org.mardep.ssrs.constant;

public enum ChangePasswordResult {

	SUCCESSFUL(0, "Change password successful!"),
	PASSWORD_INCORRECT(-1, "Password incorrect!"), 
	NEWPASSWORD_NOT_MATCH(-2, "New password does not match with the confirm password!")
	
	;
	private int id;
	private String message;
	
	ChangePasswordResult(int id, String message){
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
