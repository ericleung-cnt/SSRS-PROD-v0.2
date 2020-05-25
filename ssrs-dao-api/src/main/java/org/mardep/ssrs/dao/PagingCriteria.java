package org.mardep.ssrs.dao;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagingCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private long startRow;
	private long endRow;
	private boolean fetchAll = false;
	
	public PagingCriteria() {
		super();
	}
	
	public PagingCriteria(boolean fetchAll) {
		super();
		this.fetchAll = fetchAll;
	}
	
	public PagingCriteria(long startRow, long endRow) {
		super();
		this.startRow = startRow;
		this.endRow = endRow;
	}

	public long getMaxResult(){
		return (endRow-startRow);
	}
}
