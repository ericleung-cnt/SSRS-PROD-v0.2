package org.mardep.ssrs.report.generator;

import org.springframework.stereotype.Service;

/**
 * for ProToFull With Bill of Sale & POA
 *
 */
@Service("RPT_ProToFullAcd")
public class RPT_ProToFullAcd extends RPT_Acd {

	public RPT_ProToFullAcd() {
		super("ProToFull_ACD.jrxml");
	}
}
