package org.mardep.ssrs.dms.ocr.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlEng2A_Discharged {
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
	private String dateOfEngagement;
	
	@Getter
	@Setter
	private String placeOfEngagement;
	
	@Getter
	@Setter
	private String capacity;
	
	@Getter
	@Setter
	private String dateOfDischarge;
	
	@Getter
	@Setter
	private String placeOfDischarge;
	
	public Date getDischargeDate() {
		return convertDateFromString(this.dateOfDischarge);
	}
	
	public Date getEngagementDate() {
		return convertDateFromString(this.dateOfEngagement);
	}
	
	private Date convertDateFromString(String requestDate) {
		if (requestDate!=null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
				Date date1 = sdf.parse(requestDate.replace("/", "-"));
				return date1;
			} catch (ParseException ex) {
				System.out.println("parse exception: " + ex.getMessage());
				ex.printStackTrace();
			}
			return null;
		}
		return null;
	}
	

}
