package org.mardep.ssrs.report.bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class KeyValue {

	@Getter
	final public String key;

	@Getter
	final public String value;
	 
}
