package org.mardep.ssrs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortByCriteria implements Serializable{

	private static final long serialVersionUID = 1L;
	
	final List<String> sortByFieldsList;
	
	private boolean distinct;
	
	public SortByCriteria(){
		this(new ArrayList<String>());
	}

	public SortByCriteria(List<String> sortByFieldsList, boolean distinct) {
		super();
		this.sortByFieldsList = sortByFieldsList;
		this.distinct = distinct;
	}

	public SortByCriteria(final List<String> sortByFieldsList){
		this.sortByFieldsList = sortByFieldsList ;
	}
	
	public SortByCriteria add(String field){
		this.sortByFieldsList.add(field);
		return this;
	}
}
