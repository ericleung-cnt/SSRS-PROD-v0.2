package org.mardep.ssrs.service.demandNote;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.domain.constant.DemandNoteRefundStatus;
import org.mardep.ssrs.domain.constant.ReceiptStatus;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.service.DemandNoteService;
import org.mardep.ssrs.service.IDemandNoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class DemandNoteRefundServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired 
	IDemandNoteService dnSvc;
	
	DemandNoteHeader _header;
	List<DemandNoteReceipt> _receipts;
	List<DemandNoteRefund> _refunds;
	
	@Test
	public void testGetPossibleRefundAmt_EqualReceiptAmt_NoRefundAmt() {
		double expectedAmt = 123.45;
		_header = new DemandNoteHeader();
		_header.setAmount(new BigDecimal("123.45"));
		_receipts = new ArrayList<DemandNoteReceipt>();
		_refunds = new ArrayList<DemandNoteRefund>();
		
		DemandNoteReceipt receipt = new DemandNoteReceipt();
		receipt.setAmount(new BigDecimal("123.45"));
		_receipts.add(receipt);
			
		DemandNoteService dnSvc = new DemandNoteService();
		double calcAmt = dnSvc.getPossibleRefundAmt("", _header, _receipts, _refunds);
		assertEquals(expectedAmt, calcAmt, 0);
	}
	
	@Test
	public void testGetPossibleRefundAmt_EqualReceiptAmt_CancelledReceipt_NoRefundAmt() {
		double expectedAmt = 123.45;
		_header = new DemandNoteHeader();
		_header.setAmount(new BigDecimal("123.45"));
		_receipts = new ArrayList<DemandNoteReceipt>();
		_refunds = new ArrayList<DemandNoteRefund>();
		
		DemandNoteReceipt receipt = new DemandNoteReceipt();
		receipt.setCanAdjStatus(ReceiptStatus.CANCELLED.getCode());
		receipt.setAmount(new BigDecimal("123.45"));
		_receipts.add(receipt);
			
		expectedAmt = 0;
		
		DemandNoteService dnSvc = new DemandNoteService();
		double calcAmt = dnSvc.getPossibleRefundAmt("", _header, _receipts, _refunds);
		assertEquals(expectedAmt, calcAmt, 0);
	}

	@Test
	public void testGetPossibleRefundAmt_EqualReceiptAmt_ApprovedRefundAmt() {
		double expectedAmt = 123.45;
		_header = new DemandNoteHeader();
		_header.setAmount(new BigDecimal("123.45"));
		_receipts = new ArrayList<DemandNoteReceipt>();
		_refunds = new ArrayList<DemandNoteRefund>();
		
		DemandNoteReceipt receipt = new DemandNoteReceipt();
		receipt.setAmount(new BigDecimal("123.45"));
		_receipts.add(receipt);
			
		DemandNoteRefund refund = new DemandNoteRefund();
		refund.setRefundStatus(DemandNoteRefundStatus.APPROVED.toString());
		refund.setRefundAmount(new BigDecimal("110.3"));
		_refunds.add(refund);
		
		expectedAmt = expectedAmt - refund.getRefundAmount().doubleValue();
		
		DemandNoteService dnSvc = new DemandNoteService();
		double calcAmt = dnSvc.getPossibleRefundAmt("", _header, _receipts, _refunds);
		assertEquals(expectedAmt, calcAmt, 0);		
	}

	@Test
	public void testGetPossibleRefundAmt_EqualReceiptAmt_RejectedRefundAmt() {
		double expectedAmt = 123.45;
		_header = new DemandNoteHeader();
		_header.setAmount(new BigDecimal("123.45"));
		_receipts = new ArrayList<DemandNoteReceipt>();
		_refunds = new ArrayList<DemandNoteRefund>();
		
		DemandNoteReceipt receipt = new DemandNoteReceipt();
		receipt.setAmount(new BigDecimal("123.45"));
		_receipts.add(receipt);
			
		DemandNoteRefund refund = new DemandNoteRefund();
		refund.setRefundStatus(DemandNoteRefundStatus.REJECTED.toString());
		refund.setRefundAmount(new BigDecimal("110.3"));
		_refunds.add(refund);
			
		//expectedAmt = expectedAmt - refund.getRefundAmount().doubleValue();
		
		DemandNoteService dnSvc = new DemandNoteService();
		double calcAmt = dnSvc.getPossibleRefundAmt("", _header, _receipts, _refunds);
		assertEquals(expectedAmt, calcAmt, 0);				
	}

}
