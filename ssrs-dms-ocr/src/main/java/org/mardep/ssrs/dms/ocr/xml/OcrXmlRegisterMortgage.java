package org.mardep.ssrs.dms.ocr.xml;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlRegisterMortgage {
	
	@Getter
	@Setter
	private String pdfName;
	
	@Getter
	@Setter
	private String shipName;
	
	@Getter
	@Setter
	private String officialNumber;
	
	@Getter
	@Setter
	private String nameOfMortgagee;
	
	@Getter
	@Setter
	private String addressOfMortgagee;
}
