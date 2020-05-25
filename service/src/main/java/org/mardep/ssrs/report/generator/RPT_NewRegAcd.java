package org.mardep.ssrs.report.generator;

import org.springframework.stereotype.Service;

/**
 * for New Registry(All Cert for delivery)
 *
 */
@Service("RPT_NewRegAcd")
public class RPT_NewRegAcd extends RPT_Acd {
	
	public RPT_NewRegAcd() {
		super("NewReg_ACD.jrxml");
	}
}
