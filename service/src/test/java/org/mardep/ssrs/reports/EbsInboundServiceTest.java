package org.mardep.ssrs.reports;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.dao.dns.ISoapMessageOutDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dao.user.ITransactionLockDao;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.user.TransactionLock;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.ebs.EbsInboundService;
import org.mardep.ssrs.ebs.pojo.inbound.createDn4Atc.CreateDn4AtcRequest;
import org.mardep.ssrs.ebs.pojo.inbound.createDn4Atc.CreateDn4AtcResponse;
import org.mardep.ssrs.ebs.pojo.inbound.download.DownloadDnRequest;
import org.mardep.ssrs.ebs.pojo.inbound.download.DownloadTranscriptRequest;
import org.mardep.ssrs.ebs.pojo.inbound.isSettled.IsSettledRequest;
import org.mardep.ssrs.ebs.pojo.inbound.outstandingAtcList.OutstandingAtcListRequest;
import org.mardep.ssrs.ebs.pojo.inbound.outstandingDnList.OutstandingDnListRequest;
import org.mardep.ssrs.ebs.pojo.inbound.receipt.ReceiptRequest;
import org.mardep.ssrs.ebs.pojo.inbound.rejectAutopay.RejectAutopayRequest;
import org.mardep.ssrs.ebs.pojo.inbound.retrieveVessel4Transcript.RetrieveVessel4TranscriptRequest;
import org.mardep.ssrs.ebs.pojo.inbound.retrieveVesselByIMO.RetrieveVesselByIMORequest;
import org.mardep.ssrs.ebs.pojo.inbound.searchVessel4Transcript.SearchVessel4TranscriptRequest;
import org.mardep.ssrs.ebs.pojo.inbound.settleDn.SettleDnRequest;
import org.mardep.ssrs.ebs.pojo.inbound.settleDn.SettleDnResponse;
import org.mardep.ssrs.ebs.pojo.inbound.shipReg.ShipRegRequest;
import org.mardep.ssrs.ebs.pojo.inbound.transcriptApp.SubmitReq;
import org.mardep.ssrs.ebs.pojo.inbound.transcriptApp.SubmitTranscriptResponse;
import org.mardep.ssrs.ebs.pojo.inbound.transcriptApp.ValidateReq;
import org.mardep.ssrs.ebs.pojo.inbound.updateAtc.UpdateAtcRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class EbsInboundServiceTest {

	@Autowired EbsInboundService ebs;

	@Autowired
	IOwnerDao ownerDao;

	@Autowired
	IDemandNoteHeaderDao dnHeaderDao;

	@Autowired
	ITransactionLockDao lockDao;

	@Autowired
	IRegMasterDao rmDao;
	@Autowired
	IDemandNoteItemDao itemDao;
	@Autowired
	ISoapMessageOutDao soapOut;

	@Before
	public void init() {
		User user = new User();
		user.setId("TESTER");
		UserContextThreadLocalHolder.setCurrentUser(user);
	}
	@Test
	@Transactional
	public void testEbs() throws NumberFormatException, ParseException, IOException {
		String[] dnList = new String[]{"051600000015192","051600000245193","051600000291190",
				"051600000291190","051600000290193","051600000290193","051600000289197",
				"051600000288190","051600000287193","051600000287193","051600000286196",
		"051600000282198"};
		for (String d : dnList) {
			download(d);
		}

		String applNo = ownerDao.findAll().get(0).getApplNo();
		String address3 = "Central";
		String address2 = "Harbour Road";
		String address1 = "3/F";
		boolean autopay = true;
		BigDecimal amount = BigDecimal.ONE;
		String dnNo = dnHeaderDao.findAll().get(0).getDemandNoteNo();
		RegMaster rm = rmDao.findById(applNo);
		String imoNo = rm.getImoNo();
		String rejectedBy = "FIN";
		String officialNo = rm.getOffNo();
		String vesselName = rm.getRegName();

		ebs.createDn4Atc(new CreateDn4AtcRequest(applNo, amount, autopay, address1, address2, address3));
		ebs.downloadDn(new DownloadDnRequest(dnNo));
		ebs.downloadTranscript(new DownloadTranscriptRequest("1999/001,201904172359"));
		ebs.downloadTranscript(new DownloadTranscriptRequest("1999/001,201904179"));
		ebs.isSettled(new IsSettledRequest(Arrays.asList(dnNo)));
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date nextYear = format.parse(String.valueOf(Long.valueOf(format.format(new Date())) + 10000));
		rm.setAtfDueDate(nextYear);
		rm.setDeRegTime(null);
		rm.setImoNo(imoNo);
		rmDao.save(rm);
		DemandNoteItem item = new DemandNoteItem();
		item.setApplNo(applNo);
		item.setFcFeeCode("01");
		item.setAmount(new BigDecimal(110));
		item.setChargedUnits(1);
		item.setChgIndicator("Y");
		item.setGenerationTime(new Date());
		item.setUserId("UNIT TEST");
		itemDao.save(item);


		ebs.outstandingAtcList(new OutstandingAtcListRequest());

		ebs.outstandingDnList(new OutstandingDnListRequest(Arrays.asList(imoNo)));
		ebs.receipt(new ReceiptRequest(dnNo, null));
		ebs.rejectAutopay(new RejectAutopayRequest(rejectedBy, Arrays.asList(dnNo)));
		ebs.retrieveVessel4Transcript(new RetrieveVessel4TranscriptRequest(null, officialNo));
		ebs.retrieveVesselByIMO(new RetrieveVesselByIMORequest(imoNo));
		ebs.searchVessel4Transcript(new SearchVessel4TranscriptRequest(""));
		Calendar now = Calendar.getInstance();
		SubmitTranscriptResponse submitted = ebs.submitTranscriptApp(new SubmitReq(applNo, now, "BILL PERSON", new BigDecimal(6), false));
		ebs.settleDn(new SettleDnRequest(dnNo, null, 1, Calendar.getInstance()));
		ebs.settleDn(new SettleDnRequest(null, submitted.txnCode, 6, Calendar.getInstance()));
		ebs.shipReg(new ShipRegRequest(vesselName, null, null));
		ebs.submitTranscriptApp(new SubmitReq(applNo, now, "BILL PERSON", BigDecimal.valueOf(6), false));
		Calendar past = Calendar.getInstance();
		past.set(Calendar.YEAR, 1970);
		ebs.submitTranscriptApp(new SubmitReq(applNo, past, "BILL PERSON", BigDecimal.valueOf(6), false));
		ebs.submitTranscriptApp(new SubmitReq("x", past, "BILL PERSON", BigDecimal.valueOf(6), false));
		ebs.updateAtc(new UpdateAtcRequest(applNo, true));
		ebs.validateTranscriptApp(new ValidateReq(applNo, now));
		TransactionLock lock = new TransactionLock(applNo, null, "x", new Date());
		lock = lockDao.save(lock);
		ebs.validateTranscriptApp(new ValidateReq(applNo, now));
		lockDao.delete(lock);
		ebs.validateTranscriptApp(new ValidateReq(applNo, now));
		ebs.validateTranscriptApp(new ValidateReq("xxxxxx", now));
	}
	private void download(String dnNo) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(dnNo + ".pdf")) {
			IOUtils.write(ebs.downloadDn(new DownloadDnRequest(dnNo)).pdf, fos);
		}
	}

	@Test
	public void testCreateDemandNoteAndSettle() throws InterruptedException, TimeoutException {
		Date startTime = new Date();


		CreateDn4AtcRequest req = new CreateDn4AtcRequest();
		req.applNo = "1999/001";
		req.address1 = "hk hk";
		req.amount = new BigDecimal(2010);
		req.autopay = true;
		CreateDn4AtcResponse resp = ebs.createDn4Atc(req);
		String dnNo = resp.docId;

		while (soapOut.findResponseAfter(startTime).isEmpty()) {
			Thread.sleep(2000);
			if (System.currentTimeMillis() - startTime.getTime() > 20000) {
				throw new TimeoutException();
			}
		}

		SettleDnRequest settle = new SettleDnRequest();
		settle.dnNo = dnNo;
		settle.paymentMethod = 1;
		settle.valueDate = Calendar.getInstance();
		SettleDnResponse response = ebs.settleDn(settle);
		System.out.println(response.docId);
		while (soapOut.findResponseAfter(startTime).isEmpty()) {
			Thread.sleep(2000);
			if (System.currentTimeMillis() - startTime.getTime() > 20000) {
				throw new TimeoutException();
			}
		}
	}

}
