package org.mardep.ssrs.service.demandNote;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.dn.IDemandNoteRefundDao;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatus;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.service.DemandNoteService;
import org.mardep.ssrs.service.DnsDemandNoteService;
import org.mardep.ssrs.service.IDemandNoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
//@ContextConfiguration("/test-context.xml")
@Transactional
public class DnsDemandNoteServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

//	@Autowired 
//	@Qualifier("demandNoteService")
//	IDemandNoteService dnSvc;
	
	@Autowired
	IDemandNoteHeaderDao demandNoteHeaderDao;

	@Autowired
	IDemandNoteRefundDao refundDao;

	@Test
	public void testHandleApprovedRefundRequest() throws ParseException {
		DnsDemandNoteService svc = new DnsDemandNoteService();
		RefundStatus dnsObj = new RefundStatus();
		
		dnsObj.setDnNo("104382");
		dnsObj.setUserCode("dns test");
		dnsObj.setOfficeCode("17");
		dnsObj.setBillCode("05");
		dnsObj.setDnStatus(16);
		dnsObj.setRefundVouchorNo("123456");
		dnsObj.setRePayDate(new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2019-10-21 16:12"));
		dnsObj.setReVouDate(new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2019-10-22 16:13"));
		dnsObj.setReFundAmount(BigDecimal.valueOf(260.00d));
		
		DemandNoteHeader dnHeader = demandNoteHeaderDao.findById(dnsObj.getDnNo());

		svc.handleApprovedRefundRequest(dnsObj);
		svc.updatePaymentStatus(dnHeader);
	}

	@Test
	public void testHandleRejectedRefundRequest() throws ParseException {
		DnsDemandNoteService svc = new DnsDemandNoteService();
		RefundStatus dnsObj = new RefundStatus();
		
		dnsObj.setDnNo("104382");
		dnsObj.setUserCode("dns test");
		dnsObj.setOfficeCode("17");
		dnsObj.setBillCode("05");
		dnsObj.setDnStatus(16);
		dnsObj.setRefundVouchorNo("123456");
		dnsObj.setRePayDate(new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2019-10-21 16:12"));
		dnsObj.setReVouDate(new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2019-10-22 16:13"));
		dnsObj.setReFundAmount(BigDecimal.valueOf(260.00d));
		
		DemandNoteHeader dnHeader = demandNoteHeaderDao.findById(dnsObj.getDnNo());

		svc.handleRejectedRefundRequest(dnsObj);
		svc.updatePaymentStatus(dnHeader);
	}
}
