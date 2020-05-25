package org.mardep.ssrs.report.bean;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public enum TenYearRangeAge {

	_20(""),
	_21_TO_30(""),
	_31_TO_40(""),
	_41_TO_50(""),
	_51_TO_60(""),
	_61(""),
	;
	
	TenYearRangeAge(String name){
		this.name = name;
	}
	public String name;
	public Date from;
	public Date to;
	
	public String getName(){
		return name;
	}
	
	public TenYearRangeAge instance(Date date){
//		Date from = null;
//		Date to = null;
		switch (this) {
		case _20:
			to = date;
			from = DateUtils.addYears(date, -20);
			break;
		case _21_TO_30:
			to = DateUtils.addDays(DateUtils.addYears(date, -20), -1);
			from = DateUtils.addYears(date, -30);
			break;
		case _31_TO_40:
			to = DateUtils.addDays(DateUtils.addYears(date, -30), -1);
			from = DateUtils.addYears(date, -40);
			break;
		case _41_TO_50:
			to = DateUtils.addDays(DateUtils.addYears(date, -40), -1);
			from = DateUtils.addYears(date, -50);
			break;
		case _51_TO_60:
			to = DateUtils.addDays(DateUtils.addYears(date, -50), -1);
			from = DateUtils.addYears(date, -60);
			break;
		case _61:
			to = DateUtils.addDays(DateUtils.addYears(date, -60), -1);
			from = DateUtils.addYears(date, -120);
			break;
		default:
			break;

		}
		return this;
	}
	
	public Date getFrom(){
		return from;
	}
	public Date getTo(){
		return to;
	}
	
}
