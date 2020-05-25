package org.mardep.ssrs.dao;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class PredicateCriteria implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum PredicateType{
		LIKE,
		LIKE_OR,
		LIKE_IGNORE_CASE,
		EQUAL,
		EQUAL_OR,
		STARTSWITH,
		GREATER_OR_EQUAL,
		LESS_OR_EQUAL,
		IN,
		NOT_IN_OR_NULL,
		IS_NULL,
		FIELDS_EQUAL,
		FIELDS_NOT_EQUAL,
		FIELDS_LESS_THAN,
		FIELDS_LESS_THAN_OR_NULL,
		;
		
	}
	
	private String key;
	private String path;
	private PredicateType predicateType;
	private String[] fieldsArray;
	
	public PredicateCriteria(String key, PredicateType predicateType){
		this(key, key, predicateType);
	}
	
	public PredicateCriteria(String key, String path, PredicateType predicateType){
		this.key = key;
		this.path = path;
		this.predicateType = predicateType;
	}
	public PredicateCriteria(String key, String path, PredicateType predicateType, String[] fieldsArray){
		this.key = key;
		this.path = path;
		this.predicateType = predicateType;
		this.fieldsArray = fieldsArray;
	}
}
