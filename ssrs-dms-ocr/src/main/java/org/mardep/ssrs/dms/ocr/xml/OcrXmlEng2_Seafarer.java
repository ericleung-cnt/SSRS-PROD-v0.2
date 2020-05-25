package org.mardep.ssrs.dms.ocr.xml;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlEng2_Seafarer {
	@Getter
	@Setter
	private String refNo;
	
	@Getter
	@Setter
	private String seafarerName;
	
	@Getter
	@Setter
	private String serb;
	
	@Getter
	@Setter
	private String dateOfBirth;
	
	@Getter
	@Setter
	private String gender;
	
	@Getter
	@Setter
	private String nationality;
	
	@Getter
	@Setter
	private String placeofBirth;
	
	@Getter
	@Setter
	private String address1;
	
	@Getter
	@Setter	
	private String address2;
	
	@Getter
	@Setter
	private String nextOfKin;
	
	@Getter
	@Setter
	private String relationship;
	
	@Getter
	@Setter
	private String capacity;
	
	@Getter
	@Setter
	private String cert;
	
	@Getter
	@Setter
	private String wages;
	
	@Getter
	@Setter
	private String wagesUnit;
	
	@Getter
	@Setter
	private String dateOfEngagement;
	
	@Getter
	@Setter
	private String placeOfEngagement;
	
	@Getter
	@Setter
	private String dateOfDischarge;
	
	@Getter
	@Setter
	private String placeOfDischarge;
	
	public Date getBirthDate() throws ParseException {
		OcrXmlUtility util = new OcrXmlUtility();
		return util.convertDateFromString(this.dateOfBirth);
	}

	public Date getEngageDate() throws ParseException {
		OcrXmlUtility util = new OcrXmlUtility();
		return util.convertDateFromString(this.dateOfEngagement);
	}
	
	public Date getDischargeDate() throws ParseException {
		OcrXmlUtility util = new OcrXmlUtility();
		return util.convertDateFromString(this.dateOfDischarge);
	}
		
//	private Date convertDateFromString(String requestDate) {
//		if (requestDate!=null) {
//			try {
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
//				Date date1 = sdf.parse(requestDate.replace("/", "-"));
//				return date1;
//			} catch (ParseException ex) {
//				System.out.println("parse exception: " + ex.getMessage());
//				ex.printStackTrace();
//			}
//			return null;
//		}
//		return null;
//	}
	
	public BigDecimal getSalary() {
		if (this.wages!=null) {
			return new BigDecimal(this.wages);
		} else {
			return null;
		}
	}
}
