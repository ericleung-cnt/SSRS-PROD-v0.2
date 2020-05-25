package org.mardep.ssrs.dms.ocr.xml;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import org.mardep.ssrs.domain.sr.ApplDetail;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlShipRegistration {
	
	@Getter
	@Setter
	private String pdfName;
	
	@Getter
	@Setter
	private String shipName;
	
	@Getter
	@Setter
	private String chiShipName;
	
	@Getter
	@Setter
	private String altShipName;
	
	@Getter
	@Setter
	private String presentPort;
	
	@Getter
	@Setter
	private String prevOfficialNo;
	
	@Getter
	@Setter
	private String propsedDate;
	
	@Getter
	@Setter
	private String classSociety;
	
	@Getter
	@Setter
	private String imoNo;
	
	@Getter
	@Setter
	private String ownerName;
	
	@Getter
	@Setter
	private String ownerAddress;
	
	@Getter
	@Setter
	private String demiseName;
	
	@Getter
	@Setter
	private String demiseAddress;
	
	@Getter
	@Setter
	private String presentName;
	
	@Getter
	@Setter
	private String totalShare;
	
	@Getter
	@Setter
	private String builderNameAndAddress;
	
	@Getter
	@Setter
	private String typeOfShip;
	
	@Getter
	@Setter
	private String keelLaidDate;
	
	@Getter
	@Setter
	private String materialOfHull;
	
	@Getter
	@Setter
	private String grossTonnage;
	
	@Getter
	@Setter
	private String netTonnage;
	
	@Getter
	@Setter
	private String length;
	
	@Getter
	@Setter
	private String breadth;
	
	@Getter
	@Setter
	private String mouldedDepth;
	
	@Getter
	@Setter
	private String engineMakeAndModel;
	
	@Getter
	@Setter
	private String engineType;
	
	@Getter
	@Setter
	private String noOfEngines;
	
	@Getter
	@Setter
	private String noOfShafts;
	
	@Getter
	@Setter
	private String totalEngine;
	
	@Getter
	@Setter
	private String shipManagerNameAndAddress;
	
	@Getter
	@Setter
	private String shipManagerTel;
	
	@Getter
	@Setter
	private String shipManagerFax;
	
	@Getter
	@Setter
	private String shipManagerEmail;
	
	@Getter
	@Setter
	private String rpNameAndAddress;
	
	@Getter
	@Setter
	private String rpTel;
	
	@Getter
	@Setter
	private String rpFax;
	
	@Getter
	@Setter
	private String rpEmail;
	
	public Date getProposedRegDate() throws ParseException {
		OcrXmlUtility util = new OcrXmlUtility();
		return util.convertDateFromString(this.propsedDate);
	}
	
	public BigDecimal getShare() {
		OcrXmlUtility util = new OcrXmlUtility();
		return util.convertBigDecimalFromString(this.totalShare);
	}
	
	public BigDecimal getGrossTon() {
		OcrXmlUtility util = new OcrXmlUtility();
		return util.convertBigDecimalFromString(this.grossTonnage);
	}
	
	public BigDecimal getNetTon() {
		OcrXmlUtility util = new OcrXmlUtility();
		return util.convertBigDecimalFromString(this.netTonnage);
	}
}
