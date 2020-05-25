package org.mardep.ssrs.report.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * for Report 002-006 in MMO
 * 
 * @author Leo.LIANG
 *
 */
@ToString
public class TenYearRangeRank extends AbstractYearRangeRank{

	public TenYearRangeRank(String department, String rank) {
		super(department, rank);
	}

	public void add(TenYearRangeRank other){
		
	}
	
	@Getter
	@Setter
	long age20; // 20 below
	public void age20Add(long accum){
		age20 = age20 + accum;
	}
	
	@Getter
	@Setter
	long age2130;
	public void age2130Add(long accum){
		age2130 = age2130 + accum;
	}
	
	@Getter
	@Setter
	long age3140;
	public void age3140Add(long accum){
		age3140 = age3140 + accum;
	}
	
	@Getter
	@Setter
	long age4150;
	public void age4150Add(long accum){
		age4150 = age4150 + accum;
	}
	
	@Getter
	@Setter
	long age5160;
	public void age5160Add(long accum){
		age5160 = age5160 + accum;
	}
	
	@Getter
	@Setter
	long age61;
	public void age61Add(long accum){
		age61 = age61 + accum;
	}
	
	@Setter
	long rankTotal; 
	
	public long getRankTotal(){
		return age20 + age2130 + age3140 + age4150 + age5160  + age61;
	}

}
