package org.mardep.ssrs.dns;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dns.pojo.common.ReceiptInfo;
import org.mardep.ssrs.dns.pojo.common.ReceiptItem;
import org.mardep.ssrs.dns.pojo.common.RefundAction;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatus;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusResponse;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatus;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatusResponse;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatus;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusResponse;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.UpdateRefundStatus;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteInfo;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ws.SimpleWebServiceOutboundGateway;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * this is simulating sending SOAP message from ssrs to DNS
 *
 * @author LeoX.Liang1
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:/server/dns/dns-outbound.xml", "classpath:/server/dns/dns-out-test.xml"})
@ContextConfiguration(locations = { "classpath:/server/dns/dns-out-test.xml"})
public class DnsOutboundTest {
	private final static Logger logger=LoggerFactory.getLogger(DnsOutboundTest.class);

	@Autowired
	private org.mardep.ssrs.dns.test.DnsOutService dnsOutService;

//	@Autowired
//	private SimpleWebServiceOutboundGateway gateway;
//
//	@Before
//	public void init(){
//		gateway.setm
//	}

	@Test
	public void testWriteOff() throws ParseException {
		DemandNoteStatusRequest demandNoteStatusRequest = new DemandNoteStatusRequest();
		DemandNoteStatus updateDNStatus = new DemandNoteStatus();
		updateDNStatus.setDnNo("051700000330193");
		updateDNStatus.setUserCode("DNS");
		updateDNStatus.setOfficeCode("17");
		updateDNStatus.setRemark("testing 14/08/2019 15:58");
		updateDNStatus.setBillCode("05");
		Date writeOffDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-08-14 16:00:45");
		updateDNStatus.setWriteOffDate(writeOffDate);
		updateDNStatus.setDnStatus(11);

		demandNoteStatusRequest.setUpdateDNStatus(updateDNStatus);
		dnsOutService.createDemandNote(demandNoteStatusRequest);
	}
    @Test
    public void testUpdateDemandNote() throws IOException {
    	List<DemandNoteItem> itemList = new ArrayList<DemandNoteItem>();
    	DemandNoteItem item1 = new DemandNoteItem();
    	item1.setItemNo(1);
    	itemList.add(item1);

    	item1 = new DemandNoteItem();
    	item1.setItemNo(2);
    	itemList.add(item1);
    	DemandNoteStatusRequest request = new DemandNoteStatusRequest();
    	request.setControlId("-1");
    	DemandNoteInfo info = new DemandNoteInfo();
    	info.setDnNo("1234");
    	info.setItemList(itemList);

    	DemandNoteStatus demandNoteStatus = new DemandNoteStatus();
    	demandNoteStatus.setBillCode("60");
    	request.setUpdateDNStatus(demandNoteStatus);
    	DemandNoteStatusResponse result = dnsOutService.createDemandNote(request);
    	logger.info("####Result:{}", result);
    }

    @Test
    public void testUpdateDemandNotePayment() {
    	List<ReceiptItem> itemList = new ArrayList<ReceiptItem>();
    	ReceiptItem item1 = new ReceiptItem();
    	item1.setPaymentType("10");
    	itemList.add(item1);

    	item1 = new ReceiptItem();
    	item1.setPaymentType("70");
    	itemList.add(item1);
    	ReceiptStatusRequest request = new ReceiptStatusRequest();
    	request.setControlId("-1");

    	ReceiptInfo info = new ReceiptInfo();
    	info.setDnNo("051600000027195");
    	info.setReceiptNo("314159");
    	info.setReceiptDate(new Date());
    	info.setMachineID("Test Machine");
    	info.setReceiptAmount(new BigDecimal(200));
    	info.setPaymentList(itemList);

    	ReceiptStatus rs = new ReceiptStatus();
    	rs.setReceipt(info);
    	rs.setAction(org.mardep.ssrs.dns.pojo.common.CreateReceiptAction.U);
    	request.setCreateReceipt(rs);
    	ReceiptStatusResponse result = dnsOutService.updateDemandNotePayement(request);
    	logger.info("####Result:{}", result);
    }

    @Test
    public void testRefund() {
    	RefundStatusRequest refundRequest = new RefundStatusRequest();
    	refundRequest.setControlId("-1");
    	RefundStatus info = new RefundStatus();
    	info.setDnNo("051700000673191");
    	info.setRefId("18");
    	info.setBillCode("05");
    	info.setUserCode("DNS");
    	info.setOfficeCode("17");
    	UpdateRefundStatus urs = new UpdateRefundStatus();
    	urs.setRefund(info);
    	urs.setAction(RefundAction.A);
    	refundRequest.setUpdateRefundStatus(urs);
    	RefundStatusResponse result = dnsOutService.refund(refundRequest);
    	logger.info("####Result:{}", result);
    }
}