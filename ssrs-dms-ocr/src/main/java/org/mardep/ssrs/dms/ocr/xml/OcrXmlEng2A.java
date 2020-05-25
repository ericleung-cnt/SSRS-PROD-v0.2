package org.mardep.ssrs.dms.ocr.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlEng2A {
	@Getter
	@Setter
	private String nameOfShip;

	@Getter
	@Setter
	private String officialNumber;

	@Getter
	@Setter
	private String imoNumber;
	
	@Getter
	@Setter
	private String registeredOwner;
	
	@Getter
	@Setter
	private String dateofCommencement;
	
	@Getter
	@Setter
	private String placeOfCommencement;
	
	@Getter
	@Setter
	private List<OcrXmlEng2A_Discharged> dischargedList; 
	
	@Getter
	@Setter
	private List<OcrXmlEng2A_Employment> employmentList;
	
	public Date getCommencementDate() throws ParseException {
//		if (this.dateofCommencement!=null) {
//			try {
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
//				Date date1 = sdf.parse(this.dateofCommencement.replace("/", "-"));
//				return date1;
//			} catch (ParseException ex) {
//				System.out.println("parse exception: " + ex.getMessage());
//				ex.printStackTrace();
//			}
//			return null;
//		}
//		return null;
		OcrXmlUtility dConvert = new OcrXmlUtility();
		return dConvert.convertDateFromString(this.dateofCommencement);
	}
	
	public String getCommencementDateYYMM() throws ParseException {
		Date dCommencement = getCommencementDate();
		if (dCommencement!=null) {
			SimpleDateFormat yymm = new SimpleDateFormat("yyyymm");
			return yymm.format(dCommencement);
		} else {
			return null;
		}
//		if (this.dateofCommencement!=null) {
//			try {
//				String sDate = this.dateofCommencement.replace("/", "-");
//				SimpleDateFormat sdf;
//				if (sDate.indexOf("-")<3) {
//					sdf = new SimpleDateFormat("dd-mm-yyyy");
//				} else {
//					sdf = new SimpleDateFormat("yyyy-mm-dd");
//				}
//				SimpleDateFormat yymm = new SimpleDateFormat("yyyymm");
//				Date date1 = sdf.parse(sDate);
//				return yymm.format(date1);
//			} catch (ParseException ex) {
//				System.out.println("parse exception: " + ex.getMessage());
//				ex.printStackTrace();
//			}
//			return null;
//		}
//		return null;		
	}

}
