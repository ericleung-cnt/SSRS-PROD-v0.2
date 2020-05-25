package org.mardep.ssrs.dms.ocr.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class OcrXmlEng2 {
	
	@Getter
	@Setter
	private String shipName;
	
	@Getter
	@Setter	
	private String imoNumber;
	
	@Getter
	@Setter
	private String dateOfCommencement;
	
//	@Getter
//	@Setter
//	private String refNo1;
//	
//	@Getter
//	@Setter
//	private String seafarerName1;
//	
//	@Getter
//	@Setter
//	private String serb1;
//	
//	@Getter
//	@Setter
//	private String dateOfBirth1;
//	
//	@Getter
//	@Setter
//	private String gender1;
//	
//	@Getter
//	@Setter
//	private String nationality1;
//	
//	@Getter
//	@Setter
//	private String placeofBirth1;
//	
//	@Getter
//	@Setter
//	private String address1;
//	
//	@Getter
//	@Setter
//	private String nextOfKin1;
//	
//	@Getter
//	@Setter
//	private String relationship1;
//	
//	@Getter
//	@Setter
//	private String capacity1;
//	
//	@Getter
//	@Setter
//	private String cert1;
//	
//	@Getter
//	@Setter
//	private String wages1;
//	
//	@Getter
//	@Setter
//	private String dateAndPlaceOfEngagement1;
//	
//	@Getter
//	@Setter
//	private String dateAndPlaceOfDischarge1;
//	
//	@Getter
//	@Setter
//	private String refNo2;
//	
//	@Getter
//	@Setter
//	private String seafarerName2;
//	
//	@Getter
//	@Setter
//	private String serb2;
//	
//	@Getter
//	@Setter
//	private String dateOfBirth2;
//	
//	@Getter
//	@Setter
//	private String gender2;
//	
//	@Getter
//	@Setter
//	private String nationality2;
//	
//	@Getter
//	@Setter
//	private String placeofBirth2;
//	
//	@Getter
//	@Setter
//	private String address2;
//	
//	@Getter
//	@Setter
//	private String nextOfKin2;
//	
//	@Getter
//	@Setter
//	private String relationship2;
//	
//	@Getter
//	@Setter
//	private String capacity2;
//	
//	@Getter
//	@Setter
//	private String cert2;
//	
//	@Getter
//	@Setter
//	private String wages2;
//	
//	@Getter
//	@Setter
//	private String dateAndPlaceOfEngagement2;
//	
//	@Getter
//	@Setter
//	private String dateAndPlaceOfDischarge2;
//	
//	@Getter
//	@Setter
//	private String refNo3;
//	
//	@Getter
//	@Setter
//	private String seafarerName3;
//	
//	@Getter
//	@Setter
//	private String serb3;
//	
//	@Getter
//	@Setter
//	private String dateOfBirth3;
//	
//	@Getter
//	@Setter
//	private String gender3;
//	
//	@Getter
//	@Setter
//	private String nationality3;
//	
//	@Getter
//	@Setter
//	private String placeofBirth3;
//	
//	@Getter
//	@Setter
//	private String address3;
//	
//	@Getter
//	@Setter
//	private String nextOfKin3;
//	
//	@Getter
//	@Setter
//	private String relationship3;
//	
//	@Getter
//	@Setter
//	private String capacity3;
//	
//	@Getter
//	@Setter
//	private String cert3;
//	
//	
//	@Getter
//	@Setter
//	private String wages3;
//	
//	@Getter
//	@Setter
//	private String dateAndPlaceOfEngagement3;
//	
//	@Getter
//	@Setter
//	private String dateAndPlaceOfDischarge3;
//	
//	@Getter
//	@Setter
//	private String refNo4;
//	
//	@Getter
//	@Setter
//	private String seafarerName4;
//	
//	@Getter
//	@Setter
//	private String serb4;
//	
//	@Getter
//	@Setter
//	private String dateOfBirth4;
//	
//	@Getter
//	@Setter
//	private String gender4;
//	
//	@Getter
//	@Setter
//	private String nationality4;
//	
//	@Getter
//	@Setter
//	private String placeofBirth4;
//	
//	@Getter
//	@Setter
//	private String address4;
//	
//	@Getter
//	@Setter
//	private String nextOfKin4;
//	
//	@Getter
//	@Setter
//	private String relationship4;
//	
//	@Getter
//	@Setter
//	private String capacity4;
//	
//	@Getter
//	@Setter
//	private String cert4;
//	
//	@Getter
//	@Setter
//	private String wages4;
//	
//	@Getter
//	@Setter
//	private String dateAndPlaceOfEngagement4;
//	
//	@Getter
//	@Setter
//	private String dateAndPlaceOfDischarge4;
	
	@Getter
	@Setter
	private List<OcrXmlEng2_Seafarer> seafarerList; 
	
	public Date getCommencementDate() {
		if (this.dateOfCommencement!=null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
				Date date1 = sdf.parse(this.dateOfCommencement.replace("/", "-"));
				return date1;
			} catch (ParseException ex) {
				System.out.println("parse exception: " + ex.getMessage());
				ex.printStackTrace();
			}
			return null;
		}
		return null;
	}
	
	public String getCommencementDateYYMM() {
		if (this.dateOfCommencement!=null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
				SimpleDateFormat yymm = new SimpleDateFormat("yyyymm");
				Date date1 = sdf.parse(this.dateOfCommencement.replace("/", "-"));
				return yymm.format(date1);
			} catch (ParseException ex) {
				System.out.println("parse exception: " + ex.getMessage());
				ex.printStackTrace();
			}
			return null;
		}
		return null;		
	}
}
