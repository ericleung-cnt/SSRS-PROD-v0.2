package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.dao.dn.IDemandNoteReceiptDao;
import org.mardep.ssrs.dao.dn.IDemandNoteRefundDao;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.bean.ReceiptCollected01;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * SSRS Fin Refund Report:DemandNote / REceipt / Refund / DnItem
 *  <li>DemandNoteNo
 *  <li>IssueDate
 *  <li>DueDate
 *  <li>DemandNoteStatus
 *  <li>ReceiptStatus
 *  <li>ReceiptNo
 *  <li>ReceiptDate
 *  <li>FeeCode
 *
 */
@Service("RefundReportUI")
public class RPT_FIN_Refund extends AbstractFinReport {

	public final static DateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

	@Autowired
	private IDemandNoteRefundDao demandNoteRefundDao;
	@Autowired
	private IDemandNoteReceiptDao demandNoteReceiptDao;
	@Autowired
	private IDemandNoteItemDao demandNoteItemDao;

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		final String reportTitle="Refund Report";
		inputParam.put(REPORT_TITLE, reportTitle);
		inputParam.put(REPORT_ID, "FINXXX");
		inputParam.put(USER_ID, UserContextThreadLocalHolder.getCurrentUserName());

		fillData01(inputParam);

		List<String> dummyList = new ArrayList<String>();
		dummyList.add("TEST");

		return generate(dummyList, inputParam);
	}

	private void fillData01(Map<String, Object> inputParam){
		Date from = (Date)inputParam.get(DATE_FROM);
		Date to = (Date)inputParam.get(DATE_TO);
		Date recepitDateParam = (Date)inputParam.get("receiptDate");
		final String reportSubTitle="Refund Date: From %s To %s";
		inputParam.put(REPORT_SUB_TITLE, String.format(reportSubTitle, TITLE_DATE_FORMAT.format(from), TITLE_DATE_FORMAT.format(to)));

		Map<String, BigDecimal> dnAmounts = new HashMap<String, BigDecimal>();

		to = new Date(to.getTime() + 24L*3600 * 1000 - 1);

		List<DemandNoteRefund> refundList = demandNoteRefundDao.findRefund(from, to, recepitDateParam); //TODO need to check logic

		//TODO retrieve data from DB
		List<ReceiptCollected01> list1 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list2 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list3 = new ArrayList<ReceiptCollected01>();
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int count4 = 0;
		BigDecimal cancel1 = BigDecimal.ZERO;
		BigDecimal cancel2 = BigDecimal.ZERO;
		BigDecimal cancel3 = BigDecimal.ZERO;
		BigDecimal cancel4 = BigDecimal.ZERO;
		BigDecimal recd1 = BigDecimal.ZERO;
		BigDecimal recd2 = BigDecimal.ZERO;
		BigDecimal recd3 = BigDecimal.ZERO;
		BigDecimal recd4 = BigDecimal.ZERO;
		Set<String> demandNoteNos = new HashSet<>();
		for(DemandNoteRefund refund:refundList){
			String demandNoteNo = refund.getDemandNoteNo();
			DemandNoteHeader demandNote = refund.getDemandNoteHeader();
			int type;
			if (demandNote.getEbsFlag() == null) {
				type = 1;
			} else if ("1".equals(demandNote.getEbsFlag())) {
				type = 2;
			} else {
				type = 3;
			}
			ReceiptCollected01 rc01 = new ReceiptCollected01();
			rc01.setDemandNoteNo(demandNoteNo);
			rc01.setIssueDate(REPORT_DATE_FORMAT.format(demandNote.getGenerationTime()));
			rc01.setDueDate(REPORT_DATE_FORMAT.format(demandNote.getDueDate()));
			BigDecimal demandNoteAmount = demandNote.getAmount();
//			totalDemandNoteAmount = totalDemandNoteAmount.add(demandNoteAmount);
			dnAmounts.put(demandNoteNo, demandNoteAmount);
			rc01.setAmount(demandNoteAmount);
			rc01.setDemandNoteStatus("REFUND"); //TODO

			List<DemandNoteItem> items = demandNoteItemDao.findByDemandNoteNo(demandNoteNo);
			List<String> feeCodes = new ArrayList<String>();
			for(DemandNoteItem item:items){
				feeCodes.add(item.getFcFeeCode());
			}
			rc01.setFeeCode(StringUtils.join(feeCodes, " "));

			List<DemandNoteReceipt> receipts = demandNoteReceiptDao.findByDemandNoteNo(demandNoteNo);
			final String paymentStatusStr = demandNote.getPaymentStatusStr();

			boolean skipAddtoList=false;
			if(receipts!=null && receipts.size()>0){
				for(DemandNoteReceipt r:receipts){
					//TODO how to handle if multiple Receipt.
					rc01.setReceiptStatus(paymentStatusStr);
					rc01.setReceiptNo(r.getReceiptNo());
					Date receiptDate = r.getInputTime();
					if(recepitDateParam!=null && !DateUtils.truncate(recepitDateParam, Calendar.DATE).equals(DateUtils.truncate(receiptDate, Calendar.DATE))){
						skipAddtoList = true;
						break;
					}
					rc01.setReceiptDate(REPORT_DATE_FORMAT.format(receiptDate));
				}
			}
			if(skipAddtoList) continue;// not add to result list;
			rc01.setBillName(demandNote.getBillName());

			BigDecimal refundAmount = refund.getRefundAmount();
			rc01.setRefundAmount(refundAmount);
			count4++;
			recd4 = recd4.add(refundAmount);
			switch (type) {
			case 1:
				recd1 = recd1.add(refundAmount);
				count1++;
				// use cancel amount for summary of demand note
				if (!demandNoteNos.contains(demandNoteNo)) {
					demandNoteNos.add(demandNoteNo);
					cancel1 = cancel1.add(demandNoteAmount);
					cancel4 = cancel4.add(demandNoteAmount);
				}
				rc01.setCount(BigDecimal.valueOf(count1));
				rc01.setReceivedAmount(recd1);
				rc01.setCancelledAmount(cancel1);
				list1.add(rc01);
				break;
			case 2:
				recd2 = recd2.add(refundAmount);
				count2++;
				// use cancel amount for summary of demand note
				if (!demandNoteNos.contains(demandNoteNo)) {
					demandNoteNos.add(demandNoteNo);
					cancel2 = cancel2.add(demandNoteAmount);
					cancel4 = cancel4.add(demandNoteAmount);
				}
				rc01.setCount(BigDecimal.valueOf(count2));
				rc01.setReceivedAmount(recd2);
				rc01.setCancelledAmount(cancel2);
				list2.add(rc01);
				break;
			case 3:
				recd3 = recd3.add(refundAmount);
				count3++;
				// use cancel amount for summary of demand note
				if (!demandNoteNos.contains(demandNoteNo)) {
					demandNoteNos.add(demandNoteNo);
					cancel3 = cancel3.add(demandNoteAmount);
					cancel4 = cancel4.add(demandNoteAmount);
				}
				rc01.setCount(BigDecimal.valueOf(count3));
				rc01.setReceivedAmount(recd3);
				rc01.setCancelledAmount(cancel3);
				rc01.setTitle("eBS NON-AUTOPAY");
				list3.add(rc01);
				break;
			}
		}

		if (list1.isEmpty()) {
			list1.add(new ReceiptCollected01());
		}
		if (list2.isEmpty()) {
			list2.add(new ReceiptCollected01());
		}
		if (list3.isEmpty()) {
			list3.add(new ReceiptCollected01());
		}

		list1.get(0).setTitle("NON-eBS");
		list2.get(0).setTitle("ebs AUTOPAY");
		list3.get(0).setTitle("eBS NON-AUTOPAY");

		inputParam.put("refundedCase", count4);
		inputParam.put("refundAmount", recd4);
		inputParam.put("demandNoteAmount", cancel4);

		JasperReport subreport1 = jasperReportService.getJasperReport("FIN_Refund_01.jrxml");
		inputParam.put("SUBREPORT_1", subreport1);
		inputParam.put("SUBREPORTDS_1", list1);
		inputParam.put("SUBREPORT_2", subreport1);
		inputParam.put("SUBREPORTDS_2", list2);
		inputParam.put("SUBREPORT_3", subreport1);
		inputParam.put("SUBREPORTDS_3", list3);
	}

}