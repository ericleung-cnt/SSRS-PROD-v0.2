package org.mardep.ssrs.domain.dns;

import java.util.LinkedHashMap;
import java.util.Map;

public enum InterfaceName {
	I0001("Control Table"),
	I0002("SOAP Message In"),
	I0003("SOAP Message Out");

	private String name;

	InterfaceName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return String.format("[%s] %s", super.toString(), name);
	}

	public static Map<String, String> getNameValueMap() {
		Map<String, String> enumMap = new LinkedHashMap<String, String>();
		for (InterfaceName value : InterfaceName.values()) {
			enumMap.put(value.toString(), value.getFullName());
		}
		return enumMap;
	}
}
