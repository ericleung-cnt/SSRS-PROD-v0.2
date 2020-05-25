package org.mardep.ssrs.domain.constant;

import java.util.EnumMap;
import java.util.Map;

/**
 * 
 * @author Leo.LIANG
 *
 */
public enum CwIndicator {
	
//	CANCELLED("C"),
//	DELETED("D"),
//	WRITE_OFF("W"),
//	I("I"), //TODO
	
	C("Cancel"),
//	D("Delete"),//no use any more
	W("Write Off"),
//	I("Insert"), //TODO
	
	;
	
	private String code;
	
	
	CwIndicator(String code){
		this.code =code;
	}

	public String getCode() {
		return code;
	}
	
	public static Map<CwIndicator, String> getCwIndicatorValueMap(){
		Map<CwIndicator, String> enumMap = new EnumMap<CwIndicator, String>(CwIndicator.class);
		for(CwIndicator indicator:CwIndicator.values()){
			enumMap.put(indicator, indicator.getCode());
		}
		return enumMap;
	}
 
}
