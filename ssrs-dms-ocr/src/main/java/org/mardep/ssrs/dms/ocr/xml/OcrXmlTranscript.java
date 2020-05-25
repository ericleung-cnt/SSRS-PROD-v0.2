package org.mardep.ssrs.dms.ocr.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlTranscript {
	
	@Getter
	@Setter
	private String pdfName;
	
	@Getter
	@Setter
	private String issueType;
	
	@Getter
	@Setter
	private String vesselName;

	@Getter
	@Setter
	private String officialNumber;

	@Getter
	@Setter
	private String imoNumber;

	@Getter
	@Setter
	private String forDate;

	@Getter
	@Setter
	private String financialYearEnd;
	
	@Getter
	@Setter
	private String yearEnd;

	@Getter
	@Setter
	private String applicantCompanyName;
	
	@Getter
	@Setter
	private String contactPerson;

	@Getter
	@Setter
	private String address;

	@Getter
	@Setter
	private String tel;

	@Getter
	@Setter
	private String email;
	
	@Getter
	@Setter
	private long docId;
	
	@Getter
	@Setter
	private List<OcrXmlTranscript_Ship> shipList;
	
	public Date getApplyForDate() throws ParseException {
		OcrXmlUtility util = new OcrXmlUtility();
		if (this.getForDate()!=null) {
//			try {
//			//Date date1=new SimpleDateFormat("dd/MMM/yy").parse(this.getForDate());		
//				SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", Locale.UK);  // have to add locale for MMM pattern
//				Date date1 = sdf.parse(this.getForDate());
//				return date1;
//			} catch (ParseException ex) {
//				System.out.println("parse exception: " + ex.getMessage());
//				ex.printStackTrace();
//			}
//			return null;
			return util.convertDateFromString(this.getForDate());
		} else if (this.getFinancialYearEnd()!=null) {
			String financialDate = "31/03/" + this.getFinancialYearEnd();
			return util.convertDateFromString(financialDate);
		} else if (this.getYearEnd()!=null) {
			String yearEnd= "31/12/" + this.getYearEnd();
			return util.convertDateFromString(yearEnd);
		} 
		return null;					
	}
	
}
