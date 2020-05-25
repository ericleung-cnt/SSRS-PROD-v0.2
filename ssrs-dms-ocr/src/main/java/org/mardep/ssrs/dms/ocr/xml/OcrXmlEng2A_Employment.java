package org.mardep.ssrs.dms.ocr.xml;

import java.text.ParseException;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlEng2A_Employment {

	@Getter
	@Setter
	private String refNo;
	
	@Getter
	@Setter
	private String seafarerName;
	
	@Getter
	@Setter
	private String gender;
	
	@Getter
	@Setter
	private String nationality;
	
	@Getter
	@Setter
	private String dateOfBirth;
	
	@Getter
	@Setter
	private String serb;
	
	@Getter
	@Setter
	private String capacity;
	
	@Getter
	@Setter
	private String cert;
	
	@Getter
	@Setter
	private String dateOfEngagement;
	
	@Getter
	@Setter
	private String placeOfEngagement;
	
	public Date getBirthDate() throws ParseException {
		//return convertDateFromString(this.dateOfBirth);
		OcrXmlUtility dConvert = new OcrXmlUtility();
		return dConvert.convertDateFromString(this.dateOfBirth);
	}
	
	public Date getEngageDate() throws ParseException {
		//return convertDateFromString(this.dateOfEngagement);
		OcrXmlUtility dConvert = new OcrXmlUtility();
		return dConvert.convertDateFromString(this.dateOfEngagement);
	}
}
