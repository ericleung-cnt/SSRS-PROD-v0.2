package org.mardep.ssrs.pojo.trackcode;

public enum Language {

	ENG("en"),
	SC("sc"),
	TC("hk"),
	;

	private String value;
	Language(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Language value(String value){
		try{
			for(Language v:Language.values()){
				if(value.equalsIgnoreCase(v.getValue())){
					return v;
				}
			}
		}catch(Exception ex){
		}
		return Language.ENG;

	}
}

