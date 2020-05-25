package org.mardep.ssrs.report.bean;

public class RPT_MMO_001InputParam extends AbstractInputParam {

	private static final long serialVersionUID = 1L;
	
//	private String serbNo;
//	private String seafarerId;
	
	public String getSerbNo() {
		return (String) super.get("serbNo");
	}

	public String getSeafarerId() {
		return (String) super.get("seafarerId");
	}


}
