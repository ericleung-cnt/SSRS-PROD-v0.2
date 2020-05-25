package org.mardep.ssrs.dms.ocr.xml;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlReserveShipName {

	@Getter
	@Setter
	private String pdfName;
	
	@Getter
	@Setter
	private String nameAndAddressOfApplicant;

	@Getter
	@Setter
	private String nameAndAddressOfOwner;

	@Getter
	@Setter
	private String forDate;

	@Getter
	@Setter
	private Date entryTime;

	@Getter
	@Setter
	private List<OcrXmlReserveShipName_ProposedName> proposedNameList;

	public Date getExpiryDate() throws ParseException {
		OcrXmlUtility utility = new OcrXmlUtility();
		Calendar cal = Calendar.getInstance();
		if (this.forDate!=null && !this.forDate.isEmpty()) {
			cal.setTime(utility.convertDateFromString(this.forDate));
		}
		cal.add(Calendar.YEAR,  3);
		return cal.getTime();
	}
	
	public Date getEntryTime() throws ParseException {
		OcrXmlUtility utility = new OcrXmlUtility();
		return utility.convertDateFromString(forDate);
	}
}
