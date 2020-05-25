package org.mardep.ssrs.reports;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.dn.IDemandNoteReceiptDao;
import org.mardep.ssrs.dao.dns.ISoapMessageOutDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.Action;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IDemandNoteGenerator;
import org.mardep.ssrs.service.IDemandNoteService;
import org.mardep.ssrs.service.IDnsService;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class DemandNoteTest {
	@Autowired
	IDemandNoteService demandNoteService;

	@Autowired
	IDnsService dnsService;

	@Autowired
	IVitalDocClient vitalDocClient;

	@Autowired
	IDemandNoteGenerator demandNoteGenerator;

	@Autowired
	ISoapMessageOutDao soapOut;

	@Autowired
	IDemandNoteReceiptDao receiptDao;

	@Autowired
	IRegMasterDao rmDao;

	@Before
	public void init() {
		User user = new User();
		user.setId("TESTER");
		UserContextThreadLocalHolder.setCurrentUser(user);
	}

	@Test
	public void testCancel() throws IOException, InterruptedException, Exception {
		String demandNoteNo = create(true);
		System.out.println(demandNoteNo);
		String remark = "dns test cancel";
		cancel(demandNoteNo, remark);
	}

	@Test
	public void testCreate() throws IOException, InterruptedException, Exception {
		String demandNoteNo = create(true);
		System.out.println(demandNoteNo);
	}

	/**
	 * create receipt when ebs calls settleDn, demand note receipt is saved in database,
	 * and send to dns, demand note may have created before the call
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws Exception
	 */
	@Test
	public void testReceipt() throws IOException, InterruptedException, Exception {
		System.out.println("see void org.mardep.ssrs.reports.EbsInboundServiceTest.testCreateDemandNoteAndSettle()");
	}
	@Test
	public void testRefund() throws Exception {
		DemandNoteReceipt entity = new DemandNoteReceipt();
		entity.setReceiptNo("ZSR");
		List<DemandNoteReceipt> list = receiptDao.findByCriteria(entity);
		list.sort((a,b) -> { return a.getCreatedDate().compareTo(b.getCreatedDate()); });
		DemandNoteReceipt receipt = list.get(list.size() - 1);
		String demandNoteNo = receipt.getDemandNoteNo();

		Date startTime = new Date();
		int size = soapOut.findResponseAfter(startTime).size();

		DemandNoteRefund refund = demandNoteService.refund(demandNoteNo, BigDecimal.ONE, "TODO"); //TODO
		dnsService.refundDemandNote(refund.getRefundId());

		while (size == soapOut.findResponseAfter(startTime).size()) {
			System.out.println("sleep , size " + size);
			Thread.sleep(2000);
			if (System.currentTimeMillis() - startTime.getTime() > 20000) {
				throw new TimeoutException();
			}
		}
	}
	private String create(boolean autopay) throws Exception, IOException, InterruptedException {
		DemandNoteHeader entity = new DemandNoteHeader();
		entity.setApplNo("1999/001");
		entity.setBillName("abc");
		entity.setAddress1("ad ad ad hk");
		entity.setAmount(new BigDecimal(333));
		DemandNoteItem item = new DemandNoteItem();

		Date startTime = new Date();
		int size = soapOut.findResponseAfter(startTime).size();

		item.setFcFeeCode("01");
		item.setChargedUnits(1);
		item.setActive(true);
		item.setChgIndicator("Y");
		item.setDemandNoteHeader(entity);
		item.setGenerationTime(new Date());
		item.setAmount(new BigDecimal(333));
		entity.setDemandNoteItems(Arrays.asList(item));

		DemandNoteHeader header = demandNoteService.create(entity, autopay);

		String demandNoteNo = header.getDemandNoteNo();
		dnsService.createDemandNote(demandNoteNo, Action.U, autopay);


		byte[] osContent = demandNoteGenerator.generate(demandNoteNo, autopay);
		if(osContent!=null){
			vitalDocClient.uploadDemandNote(demandNoteNo, osContent);
		}
		while (size == soapOut.findResponseAfter(startTime).size()) {
			System.out.println("sleep , size " + size);
			Thread.sleep(2000);
		}
		return demandNoteNo;
	}
	private void cancel(String demandNoteNo, String remark) throws InterruptedException {
		Date startTime = new Date();
		int size = soapOut.findResponseAfter(startTime).size();

		demandNoteService.cancel(demandNoteNo, remark);
		dnsService.createDemandNote(demandNoteNo, Action.C, true);

		while (size == soapOut.findResponseAfter(startTime).size()) {
			System.out.println("sleep , size " + size);
			Thread.sleep(2000);
		}
	}

	@Test
	public void testCreateAtc() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Function<String, String> function = new Function<String, String>() {

			@Override
			public String apply(String t) {
				try {
					Date generationTime = format.parse("20190628");
					Date compare = format.parse(t);
					int yeardiff = (int) Math.ceil((generationTime.getTime() - compare.getTime()) / 365.25 / 24 /3600 / 1000);
					System.out.println(t + " " + yeardiff + " discount? "+(yeardiff > 2 && yeardiff % 2 == 1));
					return null;
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		};
		function.apply("20160728");
		function.apply("20150728");
		function.apply("20140728");
		function.apply("20130728");
		function.apply("20160101");
		function.apply("20150101");
		function.apply("20140101");
		function.apply("20130101");
		function.apply("20160501");
		function.apply("20150501");
		function.apply("20140501");
		function.apply("20130501");


		System.out.println(demandNoteService.findUnusedByAppl("1999/01"));
		demandNoteService.createAtcItem();
	}

	@Test
	public void testCreateDni() throws ParseException {
		List<RegMaster> list = rmDao.searchVessel4Transcript("KEBS");
		assert !list.isEmpty();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		for (int i = 0; i < list.size(); i++) {
			demandNoteService.createAtcDni(Arrays.asList(list.get(i)), calendar.getTime());
			calendar.add(Calendar.DATE, 1);
		}
	}

	@Test
	public void testCheckTrackCode() {
		RegMaster result;
		result = rmDao.checkTrackCode("00111-12019-04142");
		assert (result == null);
		try {
			result = rmDao.checkTrackCode("00111-12019-04151");
			assert false;
		} catch (IllegalStateException e) {
			System.out.println("expired: " + e.getMessage());
		}
		result = rmDao.checkTrackCode("00026-02019-04201");
		assert (RegMaster.REG_STATUS_DEREGISTERED.equals(result.getRegStatus()));
		result = rmDao.checkTrackCode("00485-72019-04131");
		assert (result != null && !RegMaster.REG_STATUS_DEREGISTERED.equals(result.getRegStatus()));
	}

}
