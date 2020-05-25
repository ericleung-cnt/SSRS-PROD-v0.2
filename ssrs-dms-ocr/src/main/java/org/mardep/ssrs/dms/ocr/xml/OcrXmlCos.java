package org.mardep.ssrs.dms.ocr.xml;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlCos {
	
	@Getter
	@Setter
	private String pdfName;

	@Getter
	@Setter
	private String shipName;
	
	@Getter
	@Setter
	private String imoNumber;
	
	@Getter
	@Setter
	private String builderNameAddress;
	
	@Getter
	@Setter
	private String howPropelled;
	
	@Getter
	@Setter
	private String keelLaidDate;
	
	@Getter
	@Setter
	private String typeOfShip;
	
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
	private String depth;
	
	@Getter
	@Setter
	private String numberOfMainEngines;	// eng set num, int
	
	@Getter
	@Setter
	private String numberOfTrailShafts; // no of shafts, int
	
	@Getter
	@Setter
	private String totalEnginePower;
	
	@Getter
	@Setter
	private String mainEngineType;
	
	@Getter
	@Setter
	private String engineMake;
	
	@Getter
	@Setter
	private String model;
	
	@Getter
	@Setter	
	private long docId;
	
	@Getter
	@Setter
	private String speed;
	
	public int getEngSetNum() {
		if (this.numberOfMainEngines!=null && !this.numberOfMainEngines.isEmpty()) {
			try {
				OcrXmlUtility util = new OcrXmlUtility();
				if (util.isNumeric(this.numberOfMainEngines)) {
					return Integer.parseInt(this.numberOfMainEngines);
				} else {
					if (this.numberOfMainEngines.toUpperCase().indexOf("ONE")>=0) {
						return 1;
					} else if (this.numberOfMainEngines.toUpperCase().indexOf("TWO")>0) {
						return 2;
					} else if (this.numberOfMainEngines.toUpperCase().indexOf("THREE")>0) {
						return 3;
					}					
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return 0;
			}
		}
		return 0;
	}
	
	public int getNoOfShafts() {
		if (this.numberOfTrailShafts!=null && !this.numberOfTrailShafts.isEmpty()) {
			try {
				OcrXmlUtility util = new OcrXmlUtility();
				if (util.isNumeric(this.numberOfTrailShafts)) {
					return Integer.parseInt(this.numberOfTrailShafts);
				} else {
					System.out.println("shafts: " + this.numberOfTrailShafts.toUpperCase());
					if (this.numberOfTrailShafts.toUpperCase().indexOf("ONE")>=0) {
						return 1;
					} else if (this.numberOfTrailShafts.toUpperCase().indexOf("TWO")>0) {
						return 2;
					} else if (this.numberOfTrailShafts.toUpperCase().indexOf("THREE")>0) {
						return 3;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return 0;
			}
		}
		return 0;
	}
	
	public BigDecimal getShipGrossTonnage() {
		try {
			OcrXmlUtility util = new OcrXmlUtility();
			return util.convertBigDecimalFromString(this.grossTonnage);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new BigDecimal(0);
		}
	}
	
	public BigDecimal getShipNetTonnage() {
		try {
			OcrXmlUtility util = new OcrXmlUtility();
			return util.convertBigDecimalFromString(this.netTonnage);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new BigDecimal(0);
		}		
	}
	
	public BigDecimal getShipLength() {
		try {
			OcrXmlUtility util = new OcrXmlUtility();
			return util.convertBigDecimalFromString(this.length);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new BigDecimal(0);
		}		
	}
	
	public BigDecimal getShipBreadth() {
		try {
			OcrXmlUtility util = new OcrXmlUtility();
			return util.convertBigDecimalFromString(this.breadth);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new BigDecimal(0);
		}		
	}
	
	public BigDecimal getShipDepth() {
		try {
			OcrXmlUtility util = new OcrXmlUtility();
			return util.convertBigDecimalFromString(depth);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new BigDecimal(0);
		}		
	}
	
}
