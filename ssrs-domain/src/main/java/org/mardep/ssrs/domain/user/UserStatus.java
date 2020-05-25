package org.mardep.ssrs.domain.user;

import java.util.HashMap;
import java.util.Map;

public enum UserStatus {
	ACTIVE(10, "ACTIVE"), 
	DISABLED(20, "DISABLED"),
	
	;
	
	private String code;
	private Integer id;
	
	UserStatus(Integer id, String code){
		this.id = id;
		this.code = code;
	}

	public Integer getId() {
		return id;
	}

	public String getCode() {
		return code;
	}
	public static Map<Integer, String> getUserStatusValueMap(){
		Map<Integer, String> enumMap = new HashMap<Integer, String>();
		for(UserStatus userStatus:UserStatus.values()){
			enumMap.put(userStatus.getId(), userStatus.getCode());
		}
		return enumMap;
	}
}
