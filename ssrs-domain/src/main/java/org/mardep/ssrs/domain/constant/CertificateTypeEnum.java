package org.mardep.ssrs.domain.constant;

public enum CertificateTypeEnum {
	CERTIFIED_TRANSCRIPT("CertifiedTranscript"),
	UNCERTIFIED_TRANSCRIPT("UncertifiedTranscript"),
	NEW_COR("NewCoR"),
	REVISED_COR("RevisedCoR");
	
	private String name;
	
	CertificateTypeEnum(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
