package org.mardep.ssrs.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Operator {
	CONTAINS("contains"),
	AND("and"),
	GREATER_OR_EQUAL("greaterOrEqual"),
	GREATER_OR_EQUAL_FIELD("greaterOrEqualField"),
	LESS_OR_EQUAL("lessOrEqual"),
	ICONTAINS("iContains"),
	EQUALS("equals"),
	STARTSWITH("startsWith"),
	IN("in"),
	FIELDS_EQUAL("fieldsEqual"),
	FIELDS_NOT_EQUAL("fieldsNotEqual"),
	;
	
	protected static final Logger logger = LoggerFactory.getLogger(Operator.class);
	private String value;
	
	Operator(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Operator getEnum(String value) {
        for(Operator v : values()){
        	if(v.getValue().equalsIgnoreCase(value)) {
        		return v;
        	}
        }
        logger.warn("Enum [{}] not mapping", value);
        return null;
    }
}
