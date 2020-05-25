package org.mardep.ssrs.dns.service.inbound;

import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatusResponse;

public interface IDnService{
	
	/**
	 *
	 * process request from DNS
	 * <ul>
	 * <li>Upload_writeoff:-- Follow interface mentioned in section 4.2.2
	 * (Update demand note record status) of document MD-Redev DNS – 7 Systems
	 * Interface Specification v0.15.0
	 * 
	 * @param demandNoteStatusRequest
	 * @return
	 */
	public DemandNoteStatusResponse processDnsRequest(DemandNoteStatusRequest demandNoteStatusRequest);
}
