package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

public class SummaryOfShipsByShipType {

	@Getter
	@Setter
	private String shipcode;
	
	@Getter
	@Setter
	private String shipdesc;
	
	@Getter
	@Setter
	private String subtotal;
	
	@Getter
	@Setter
	private int onos;
	
	@Getter
	@Setter
	private BigDecimal ogross;
	
	@Getter
	@Setter
	private BigDecimal onet;
	
	@Getter
	@Setter
	private int rnos;
	
	@Getter
	@Setter
	private BigDecimal rgross;
	
	@Getter
	@Setter
	private BigDecimal rnet;
	
	@Getter
	@Setter
	private int lnos;
	
	@Getter
	@Setter
	private BigDecimal lgross;
	
	@Getter
	@Setter
	private BigDecimal lnet;
	
	@Getter
	@Setter
	private int tnos;
	
	@Getter
	@Setter
	private BigDecimal tgross;
	
	@Getter
	@Setter
	private BigDecimal tnet;

}
