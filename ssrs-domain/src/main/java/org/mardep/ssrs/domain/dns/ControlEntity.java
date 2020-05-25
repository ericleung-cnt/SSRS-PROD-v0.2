package org.mardep.ssrs.domain.dns;

import java.util.HashMap;
import java.util.Map;


public enum ControlEntity {
	
	DN(10, "DN"), //10
	RECEIPT(20, "Receipt"), //20
	REFUND(30, "Refund"), //30
	
	;
	private Integer id;
	private String code;
	
	ControlEntity(Integer id, String code){
		this.id = id;
		this.code=code;
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getCode() {
		return code;
	}
	
	public static ControlEntity getControlEntity(Integer id){
		for(ControlEntity controlEntity: ControlEntity.values()){
			if(controlEntity.getId().equals(id)) {
				return controlEntity;
			}
		}
		return null;
	}

	public static ControlEntity getControlEntity(String code){
		for(ControlEntity controlEntity: ControlEntity.values()){
			if(controlEntity.getCode().equals(code)) {
				return controlEntity;
			}
		}
		return null;
	}
	
	public static Map<Integer, String> getControlEntityValueMap(){
		Map<Integer, String> enumMap = new HashMap<Integer, String>();
		for(ControlEntity controlEntity: ControlEntity.values()){
			enumMap.put(controlEntity.getId(), controlEntity.getCode());
		}
		return enumMap;
	}
	
	public static Integer getId(String value){
		for (ControlEntity controlEntity: ControlEntity.values()){
			if (controlEntity.getCode().equalsIgnoreCase(value))
				return controlEntity.getId();
		}
		return null;
	}

}
