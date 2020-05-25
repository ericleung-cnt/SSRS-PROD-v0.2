package org.mardep.ssrs.domain.dns;

import java.util.HashMap;
import java.util.Map;


public enum ControlAction {
	
	CREATE(10, "Create"), //10
	UPDATE(20, "Update"), //20
	CANCEL(30, "Cancel"), //30
	VALUE_DATE(40, "ValueDate"), //40
	REFUND(50, "Refund"), //50
	
	;
	private Integer id;
	private String code;
	
	ControlAction(Integer id, String code){
		this.id = id;
		this.code=code;
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getCode() {
		return code;
	}
	
	public static ControlAction getControlAction(Integer id){
		for(ControlAction controlAction: ControlAction.values()){
			if(controlAction.getId().equals(id)) {
				return controlAction;
			}
		}
		return null;
	}
	
	public static ControlAction getControlAction(String code){
		for(ControlAction controlAction: ControlAction.values()){
			if(controlAction.getCode().equals(code)) {
				return controlAction;
			}
		}
		return null;
	}
	
	public static Map<Integer, String> getControlActionValueMap(){
		Map<Integer, String> enumMap = new HashMap<Integer, String>();
		for(ControlAction controlAction: ControlAction.values()){
			enumMap.put(controlAction.getId(), controlAction.getCode());
		}
		return enumMap;
	}
	
	public static Integer getId(String value){
		for (ControlAction controlAction: ControlAction.values()){
			if (controlAction.getCode().equalsIgnoreCase(value))
				return controlAction.getId();
		}
		return null;
	}

}
