package org.mardep.ssrs.dms.ocr.xml;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlCsrInitial {
	
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
	private String registeredOwnerID;
	
	@Getter
	@Setter
	private String managementCompanyID;
}
