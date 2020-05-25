package org.mardep.ssrs.domain.dn;

import lombok.Getter;
import lombok.Setter;

public class DemandNoteAging extends DemandNoteHeader {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private String agingPeriod;

	@Getter
	@Setter
	private String ebsGroup;

}
