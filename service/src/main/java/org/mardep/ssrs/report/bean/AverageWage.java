package org.mardep.ssrs.report.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * for Report 005 in MMO
 * 
 * @author Leo.LIANG
 *
 */
@EqualsAndHashCode(of={"rank"}) //by SeafarerRank
@ToString
public class AverageWage{

	@Getter
	@Setter
	String department;

	@Getter
	@Setter
	String rank;
	
	@Getter
	@Setter
	long noOfSeafarer; // 20 below
		
//	@Getter
//	@Setter
	BigDecimal averageWage = BigDecimal.ZERO; //average is incorrect by SQL Server if value ==null

	public BigDecimal getAverageWage(){
		if(noOfSeafarer==0) return BigDecimal.ZERO;
		return totalWage.divide(BigDecimal.valueOf(noOfSeafarer), 2, RoundingMode.HALF_UP);
	}
	
	@Getter
	@Setter
	BigDecimal totalWage = BigDecimal.ZERO;
	
	public AverageWage(String department, String rank){
		this.department = department;
		this.rank = rank;
	}
	public AverageWage(String rank, long noOfSeafarer, BigDecimal averageWage){
		this.rank = rank;
		this.noOfSeafarer = noOfSeafarer;
		this.averageWage = averageWage;
	}
}
