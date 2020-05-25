package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

public class RegisteredShipOwner {
	
	@Getter
	@Setter
	private String applNo;
	
	@Getter
	@Setter
	private String ownerName;
	
	@Getter
	@Setter
	private String ownerType;
	
	@Getter
	@Setter
	private BigDecimal ownerInterest;
	
	@Getter
	@Setter
	private String address1;
	
	@Getter
	@Setter
	private String address2;
	
	@Getter
	@Setter
	private String address3;
	
	public String address() {
		String s = address1 + " " + address2 + " " + address3;
		return s;
	}
}
