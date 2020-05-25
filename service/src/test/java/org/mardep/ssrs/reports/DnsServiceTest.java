package org.mardep.ssrs.reports;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dns.pojo.common.RefundAction;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatus;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusResponse;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.UpdateRefundStatus;
import org.mardep.ssrs.dns.service.inbound.IRefundService;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class DnsServiceTest {

	@Autowired
	IRefundService refundService;

	@Before
	public void init() {
		User user = new User();
		user.setId("TESTER");
		UserContextThreadLocalHolder.setCurrentUser(user);
	}
	@Test
	@Transactional
	@Rollback(false)
	public void testRefund() throws NumberFormatException, ParseException, IOException {
		RefundStatusRequest refundStatusRequest = new RefundStatusRequest();
		refundStatusRequest.setControlId("");
		UpdateRefundStatus updateRefundStatus = new UpdateRefundStatus();
		updateRefundStatus.setAction(RefundAction.A);
		RefundStatus refund = new RefundStatus();
		refund.setDnNo("051600003326196");
		refund.setUserCode("DNS");
		refund.setOfficeCode("16");
		refund.setRemark("Test Partial Refund");
		refund.setBillCode("05");
		refund.setDnStatus(3);
		refund.setRefundVouchorNo("Refund");
		Date rePayDate = new SimpleDateFormat("yyyyMMdd").parse("20190829");
		refund.setRePayDate(rePayDate);
		refund.setReVouDate(rePayDate);
		BigDecimal reFundAmount = new BigDecimal("100.00");
		refund.setReFundAmount(reFundAmount);
		updateRefundStatus.setRefund(refund);
		refundStatusRequest.setUpdateRefundStatus(updateRefundStatus);
		RefundStatusResponse processDnsRequest = refundService.processDnsRequest(refundStatusRequest);
		System.out.println(processDnsRequest);
	}

	/*
	 * <Action>A</Action>
<refund>
<dnNo>051700003334198</dnNo>
<userCode>DNS</userCode>
<officeCode>17</officeCode>
<Remark>Test Full Refund 051700003334198</Remark>
<billCode>05</billCode>
<dnStatus>16</dnStatus>
<refundVouchorNo>Refund</refundVouchorNo>
<rePayDate>2019-08-29T00:00:00.000+08:00</rePayDate>
<reVouDate>2019-08-29T00:00:00.000+08:00</reVouDate>
<reFundAmount>300.00</reFundAmount>
</refund>
</updateRefundStatus>
</rpcOp:UpdateReFundStatus>
</soapenv:Body>
</soapenv:Envelope>

	 */
	@Test
	@Transactional
	@Rollback(false)
	public void testRefund2() throws NumberFormatException, ParseException, IOException {
		RefundStatusRequest refundStatusRequest = new RefundStatusRequest();
		refundStatusRequest.setControlId("");
		UpdateRefundStatus updateRefundStatus = new UpdateRefundStatus();
		updateRefundStatus.setAction(RefundAction.A);
		RefundStatus refund = new RefundStatus();
		refund.setDnNo("051700003334198");
		refund.setUserCode("DNS");
		refund.setOfficeCode("17");
		refund.setRemark("Test Full Refund 051700003334198");
		refund.setBillCode("05");
		refund.setDnStatus(16);
		refund.setRefundVouchorNo("Refund");
		Date rePayDate = new SimpleDateFormat("yyyyMMdd").parse("20190829");
		refund.setRePayDate(rePayDate);
		refund.setReVouDate(rePayDate);
		BigDecimal reFundAmount = new BigDecimal("300.00");
		refund.setReFundAmount(reFundAmount);
		updateRefundStatus.setRefund(refund);
		refundStatusRequest.setUpdateRefundStatus(updateRefundStatus);
		RefundStatusResponse processDnsRequest = refundService.processDnsRequest(refundStatusRequest);
		System.out.println(processDnsRequest);
	}


}
