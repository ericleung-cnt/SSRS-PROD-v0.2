package org.mardep.ssrs.dao;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class Criteria implements Serializable{

	private static final long serialVersionUID = 1L;

	private String fieldName;
	private Object value;
	private Operator operator;
	private String[] fieldsArray;
	
	public Criteria(String fieldName, Object value) {
		super();
		this.fieldName = fieldName;
		this.value = value;
	}
	public Criteria(String fieldName, Object value, Operator operator) {
		super();
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
	}

	public Criteria(String fieldName, Operator operator, String[] fieldsArray) {
		super();
		this.fieldName = fieldName;
		this.operator = operator;
		this.fieldsArray = fieldsArray;
	}

}
