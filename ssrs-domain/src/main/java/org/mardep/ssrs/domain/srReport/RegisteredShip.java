package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class RegisteredShip {
	
	@Getter
	@Setter
	private String applNo;
	
	@Getter
	@Setter
	private String officalNo;
	
	@Getter
	@Setter
	private Date regDate;
	
	@Getter
	@Setter
	private String callSign;
	
	@Getter
	@Setter
	private String surveyShipType;
	
	@Getter
	@Setter
	private String shipNameEng;
	
	@Getter
	@Setter
	private String shipNameChi;
	
	@Getter
	@Setter
	private BigDecimal grossTon;
	
	@Getter
	@Setter
	private BigDecimal netTon;
	
	@Getter
	@Setter
	private Long txId;
	
	@Getter
	@Setter
	private List<RegisteredShipOwner> owners;
	
	@Getter
	@Setter
	private String shipType;

	@Getter
	@Setter
	private String shipSubType;
	
	public String getOwnersString() {
		String demiseChartererStr = "";
		String ownerStr = "";
		for (RegisteredShipOwner owner : owners) {
			if ("D".equals(owner.getOwnerType())) {
				demiseChartererStr = "\n" + owner.getOwnerName() + "\n(DEMISE CHARTERER)";
			} else {
				if ("".equals(ownerStr)) {
					ownerStr = owner.getOwnerName(); 
				} else {
					ownerStr = ownerStr + "\n" + owner.getOwnerName();
				}
			}
		}
		ownerStr = ownerStr + demiseChartererStr;
		return ownerStr;
	}
}