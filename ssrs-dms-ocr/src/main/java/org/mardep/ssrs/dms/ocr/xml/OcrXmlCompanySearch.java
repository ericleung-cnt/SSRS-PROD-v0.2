package org.mardep.ssrs.dms.ocr.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlCompanySearch {
	
	@Getter
	@Setter
	private String pdfName;
	
	@Getter
	@Setter
	private String crNumber;
	
	@Getter
	@Setter
	private String companyName;
	
	@Getter
	@Setter
	private String registeredOffice;
	
	@Getter
	@Setter
	private String placeOfIncorporaion;
	
	@Setter
	private String checkDate;	

	public Date getCheckDate() throws ParseException {
//		if (this.checkDate!=null) {
//			try {
//			//Date date1=new SimpleDateFormat("dd/MMM/yy").parse(this.getForDate());		
//				SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", Locale.UK);  // have to add locale for MMM pattern
//				Date date1 = sdf.parse(this.checkDate.replace('-','/'));
//				return date1;
//			} catch (ParseException ex) {
//				System.out.println("parse exception: " + ex.getMessage());
//				ex.printStackTrace();
//			}
//			return null;
//		} 
//		return null;		
		OcrXmlUtility util = new OcrXmlUtility();
		return util.convertDateFromString(this.checkDate);
	}

}
