package org.mardep.ssrs.domain.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Leo.LIANG
 *
 */
public enum ReceiptStatus {
	
	UPDATE("U", "Updated"), //U
	INSERT("I", "Insert"), //I
	CANCELLED("C", "Cancel"), //C
	REFUND("R", "Refund"), //R Add for refund
	PENDING("P", "Pending"), //P Add for value date> current
	
	;
	
	private String code;
	private String desc;
	
	
	ReceiptStatus(String code, String desc){
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}
	
	public String getDesc() {
		return desc;
	}

	public static ReceiptStatus get(String id){
		for(ReceiptStatus s:ReceiptStatus.values()){
			 if(id.equals(s.getCode())) return s;
		}
		return null;
	}
	
	public static Map<String, String> getReceiptStatusValueMap(){
		Map<String, String> enumMap = new HashMap<String, String>();
		for(ReceiptStatus status:ReceiptStatus.values()){
			enumMap.put(status.getCode(), status.getDesc());
		}
		return enumMap;
	}
 
}
