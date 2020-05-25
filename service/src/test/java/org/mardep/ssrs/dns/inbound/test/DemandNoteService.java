package org.mardep.ssrs.dns.inbound.test;

import javax.annotation.PostConstruct;

import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteRequest;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteResponse;
import org.mardep.ssrs.dns.processor.AbstractPojoService;

/**
 * 
 * Sim DNS service to serve processing request from SSRS
 * 
 *
 */
public class DemandNoteService extends AbstractPojoService<DemandNoteRequest, DemandNoteResponse> {

	@PostConstruct
	public void init() {
		logger.info("###### no Init ######");
	}
	
	@Override
	public DemandNoteResponse process(DemandNoteRequest demandNoteRequest) {
		logger.info("#process:{}", demandNoteRequest);
		DemandNoteResponse demandNoteResponse = new DemandNoteResponse();
		return demandNoteResponse;
	}

}
