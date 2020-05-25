package org.mardep.ssrs.domain.srReport;

import lombok.Getter;
import lombok.Setter;

public class TranscriptGrantingOffice {
	
	@Getter
	@Setter
	private String issueOfficeCode;
		
	@Getter
	@Setter
	private String issueOfficeEngDesc;
	
	@Getter
	@Setter
	private int certifiedTranscriptCount;
	
	@Getter
	@Setter
	private int uncertifiedTranscriptCount;
	
	@Getter
	@Setter
	private int totalCount;
	
	public int totalCount() {
		return certifiedTranscriptCount+uncertifiedTranscriptCount;
	}
}
