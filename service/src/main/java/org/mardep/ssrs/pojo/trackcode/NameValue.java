package org.mardep.ssrs.pojo.trackcode;

import lombok.Getter;

public class NameValue {
	@Getter
	private String name;

	@Getter
	private String value;

	public NameValue(String name, String value){
		this.name = name;
		this.value = value;
	}
}
