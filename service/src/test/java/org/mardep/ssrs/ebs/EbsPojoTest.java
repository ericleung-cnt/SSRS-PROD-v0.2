package org.mardep.ssrs.ebs;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.ebs.pojo.Result;
import org.mardep.ssrs.ebs.pojo.Vessel;
import org.mardep.ssrs.ebs.pojo.inbound.searchVessel4Transcript.SearchVessel4TranscriptRequest;
import org.mardep.ssrs.ebs.pojo.inbound.searchVessel4Transcript.SearchVessel4TranscriptResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/server/ebs/soap-common.xml","classpath:/server/ebs/ebs-sim.xml"})
public class EbsPojoTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	Jaxb2Marshaller marshaller;

//	@Autowired
//	EbsOutService ebsOutService;
	
	@Test
	public void testGenerateXml() throws Exception {
		logger.info("#####");
		SearchVessel4TranscriptRequest request = new SearchVessel4TranscriptRequest();
		request.setVesselName("Vessel Name!!!");
		
//		StringWriter sw = new StringWriter();
//		marshaller.marshal(request, new StreamResult(sw));
//		logger.info("{}", sw.toString());
		
//		ebsOutService.send(request);
		
		SearchVessel4TranscriptResponse response = new SearchVessel4TranscriptResponse();
		response.setResultType("Y");
		List<Result> resultList = new ArrayList<Result>();
		Result e = new Result();
		e.setResultCode("Y1");
		resultList.add(e);
		e = new Result();
		e.setResultCode("Y2");
		resultList.add(e);
		response.setResultList(resultList);
		
		ArrayList<Vessel> vesselList = new ArrayList<Vessel>();
		Vessel vessel = new Vessel();
		vessel.setApplNo("APPNO");
		vesselList.add(vessel);
		response.setShipList(vesselList);
//		ebsOutService.send(response);
	}

}
