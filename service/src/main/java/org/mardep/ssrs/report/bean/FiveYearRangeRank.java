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
public class FiveYearRangeRank extends AbstractYearRangeRank{

	public FiveYearRangeRank(String department, String rank) {
		super(department, rank);
	}

	@Getter
	@Setter
	long age20; // 20 below
	public void age20Add(long accum){
		age20 = age20 + accum;
	}
	
	@Getter
	@Setter
	long age2125;
	public void age2125Add(long accum){
		age2125 = age2125 + accum;
	}
	
	@Getter
	@Setter
	long age2630;
	public void age2630Add(long accum){
		age2630 = age2630 + accum;
	}
	
	@Getter
	@Setter
	long age3135;
	public void age3135Add(long accum){
		age3135 = age3135 + accum;
	}
	
	@Getter
	@Setter
	long age3640;
	public void age3640Add(long accum){
		age3640 = age3640 + accum;
	}

	@Getter
	@Setter
	long age4145;
	public void age4145Add(long accum){
		age4145 = age4145 + accum;
	}
	
	@Getter
	@Setter
	long age4650;
	public void age4650Add(long accum){
		age4650 = age4650 + accum;
	}
	
	@Getter
	@Setter
	long age5155;
	public void age5155Add(long accum){
		age5155 = age5155 + accum;
	}
	
	@Getter
	@Setter
	long age5660;
	public void age5660Add(long accum){
		age5660 = age5660 + accum;
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
		return age20 + age2125 + age2630 + age3135 + age3640 + age4145 + age4650 + age5155 + age5660 + age61;
	}

}
