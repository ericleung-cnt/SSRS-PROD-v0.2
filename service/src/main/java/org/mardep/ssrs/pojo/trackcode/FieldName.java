package org.mardep.ssrs.pojo.trackcode;

public enum FieldName {

	NAME_OF_SHIP("Name of Ship", 			"船名", 			"船名"),
	IMO_NO("IMO No.", 						"国际海事组织编号", 	"國際海事組織編號"),
	DATE_OF_REGISTRY("Date of Registry", 	"注册日期", 		"註冊日期"),
	TYPE_OF_SHIP("Type of Ship", 			"船舶类型", 		"船舶類型"),
	GROSS_TONNAGE("Gross Tonnage", 			"总吨位", 			"總噸位"),
	NET_TONNAGE("Net Tonnage", 				"净吨位", 			"淨噸位"),

	;
	String eng;
	String sc;
	String tc;
	FieldName(String eng, String sc, String tc){
		this.eng = eng;
		this.sc = sc;
		this.tc = tc;
	}
	public String getEng() {
		return eng;
	}
	public String getSc() {
		return sc;
	}
	public String getTc() {
		return tc;
	}
}
