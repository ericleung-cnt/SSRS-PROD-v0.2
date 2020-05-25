package org.mardep.ssrs.dns.pojo.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "RefundAction")
public enum RefundAction {
	A("Approve"),
	R("Reject"),
	
	;
	private String value;
	
	RefundAction(String value){
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	
}
