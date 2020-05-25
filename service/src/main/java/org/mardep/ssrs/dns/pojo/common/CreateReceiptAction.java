package org.mardep.ssrs.dns.pojo.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "createReceiptAction")
public enum CreateReceiptAction {
	U("Upload"),
	C("Cancel"),
	
	;
	private String value;
	
	CreateReceiptAction(String value){
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	
}
