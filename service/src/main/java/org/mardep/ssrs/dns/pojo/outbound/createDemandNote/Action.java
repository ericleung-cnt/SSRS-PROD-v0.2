package org.mardep.ssrs.dns.pojo.outbound.createDemandNote;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "createDemandNoteAction")
public enum Action {
	U("Upload"),
	R("Update"),
	C("Cancel"),
	
	;
	private String value;
	
	Action(String value){
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	
}
