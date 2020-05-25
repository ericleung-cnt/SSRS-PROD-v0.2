package org.mardep.ssrs.dms.ocr.xml;

import java.text.ParseException;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlCsrForm2 {
	
	@Getter
	@Setter
	private String pdfName;

	@Getter
	@Setter
	private String shipName;
	
	@Getter
	@Setter
	private String imoNo;
	
	@Getter
	@Setter
	private String lastCSR;
	
	@Getter
	@Setter
	private String dateOfChange;
	
	@Getter
	@Setter
	private String reregistrationDate;
	
	@Getter
	@Setter
	private String shipNameItem;
	
	@Getter
	@Setter
	private String registeredOwner;
	
	@Getter
	@Setter
	private String registeredOwnerID;
	
	@Getter
	@Setter
	private String registeredDemiseCharterer;
	
	@Getter
	@Setter
	private String internationalSafety;
	
	@Getter
	@Setter
	private String managementCompany;
	
	@Getter
	@Setter
	private String classificationSociety;
	
	@Getter
	@Setter
	private String recognizedOrganizationDoc;
	
	@Getter
	@Setter
	private String recognizedSecurity;
	
	@Getter
	@Setter
	private String recognizedOrganizationSafetyCert;
	
	public Date getFormApplyDate() throws ParseException {
		if (this.dateOfChange!=null && !this.dateOfChange.isEmpty()) {
			OcrXmlUtility util = new OcrXmlUtility();
			return util.convertDateFromString(this.dateOfChange);
		}
		return null;
	}
}
