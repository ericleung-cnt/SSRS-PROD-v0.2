package org.mardep.ssrs.domain.dn;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

public class DemandNoteBillingPerson {

	@Getter
	@Setter
	private String billingPerson;
	
	@Getter
	@Setter
	private String c_o;
	
	@Getter
	@Setter
	private String address1;
	
	@Getter
	@Setter
	private String address2;
	
	@Getter
	@Setter
	private String address3;
	
	@Getter
	@Setter
	private String tel;
	
	@Getter
	@Setter
	private String fax;
	
	@Getter
	@Setter
	private String email;
	
	@Getter
	@Setter
	private String billingPersonType;
}
