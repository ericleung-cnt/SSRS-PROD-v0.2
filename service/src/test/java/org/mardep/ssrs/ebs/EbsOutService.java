package org.mardep.ssrs.ebs;

import org.mardep.ssrs.ebs.pojo.inbound.searchVessel4Transcript.SearchVessel4TranscriptRequest;
import org.mardep.ssrs.ebs.pojo.inbound.searchVessel4Transcript.SearchVessel4TranscriptResponse;

public interface EbsOutService {

	public void send(SearchVessel4TranscriptRequest request);
	public void send(SearchVessel4TranscriptResponse response);
}
