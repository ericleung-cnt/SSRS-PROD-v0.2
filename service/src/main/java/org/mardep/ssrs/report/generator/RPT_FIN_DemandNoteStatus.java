package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.bean.ReceiptCollected01;
import org.mardep.ssrs.report.bean.Status02;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * SSRS Fin Demand Note Status Report
 * <li>DemandNoteNo
 * <li>IssueDate
 * <li>DueDate
 *
 */
@Service("RPT_FIN_DEMAND_NOTE_STATUS")
public class RPT_FIN_DemandNoteStatus extends AbstractFinReport {

	public final static DateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

	@Autowired
	private IDemandNoteHeaderDao demandNoteHeaderDao;

	static Map<String, String> paymentTypeMap = new HashMap<String, String>();
	static{
		paymentTypeMap.put("10", "CASH");
		paymentTypeMap.put("20", "CEHQUE");
		paymentTypeMap.put("30", "EPS");
		paymentTypeMap.put("40", "REMITTANCE");
		paymentTypeMap.put("50", "CREDIT CARD");
		paymentTypeMap.put("60", "DEPOSIT");
		paymentTypeMap.put("70", "AUTOPAY");
		paymentTypeMap.put("80", "OCTOPUS");
		paymentTypeMap.put("90", "PPS");
		paymentTypeMap.put("95", "IEPS");
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		final String reportTitle="Demand Note Status Report";
		Date from = (Date)inputParam.get(DATE_FROM);
		Date to = (Date)inputParam.get(DATE_TO);
		Date rFrom = (Date)inputParam.get("receiptDateFrom");
		Date rTo = (Date)inputParam.get("receiptDateTo");
		String subtitle = "";
		if (from != null) {
			subtitle += "Issue Date from " + TITLE_DATE_FORMAT.format(from);
			if (to != null) {
				subtitle += " to " + TITLE_DATE_FORMAT.format(to);
			}
		} else {
			if (to != null) {
				subtitle += "Issue Date before " + TITLE_DATE_FORMAT.format(to);
			}
		}
		if (rFrom != null) {
			if (subtitle.length() > 0) {
				subtitle += ", ";
			}
			subtitle += "Receipt Date from " + TITLE_DATE_FORMAT.format(rFrom);
			if (rTo != null) {
				subtitle += " to " + TITLE_DATE_FORMAT.format(rTo);
			}
		} else {
			if (rTo != null) {
				if (subtitle.length() > 0) {
					subtitle += ", ";
				}
				subtitle += "Receipt Date before " + TITLE_DATE_FORMAT.format(rTo);
			}
		}

		inputParam.put(REPORT_ID, "FINXXX");
		inputParam.put(USER_ID, UserContextThreadLocalHolder.getCurrentUserId());
		inputParam.put(REPORT_TITLE, reportTitle);
		inputParam.put(REPORT_SUB_TITLE, subtitle);

		fillData01(inputParam);

		List<String> dummyList = new ArrayList<String>();
		dummyList.add("TEST");

		return generate(dummyList, inputParam);
	}

	private void fillData01(Map<String, Object> inputParam){
		String dnStatusParam = (String)inputParam.get("dnStatus");
		String paymentStatus = (String)inputParam.get("paymentStatus");
		String sortBy = (String)inputParam.get("sortBy");
		Date from = (Date)inputParam.get(DATE_FROM);
		Date to = (Date)inputParam.get(DATE_TO);
		if (to != null) {
			to = new Date(to.getTime() + 24L * 3600 * 1000 - 1);
		}

		Date receiptDateFrom = (Date)inputParam.get("receiptDateFrom");
		Date receiptDateTo = (Date)inputParam.get("receiptDateTo");
		if (receiptDateTo != null) {
			receiptDateTo = new Date(receiptDateTo.getTime() + 24L * 3600 * 1000 - 1);
		}

		List<Object[]> list = demandNoteHeaderDao.findStatusReport(from, to, receiptDateFrom, receiptDateTo, dnStatusParam, paymentStatus, sortBy);
		Set<String> demandNoteNos = new HashSet<>();
		List<ReceiptCollected01> list1 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list2 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list3 = new ArrayList<ReceiptCollected01>();
		// IEPS
		List<ReceiptCollected01> listIEPS = new ArrayList<ReceiptCollected01>();
		
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int count4 = 0;
		int countIEPS = 0;
		
		BigDecimal recd1 = BigDecimal.ZERO;
		BigDecimal recd2 = BigDecimal.ZERO;
		BigDecimal recd3 = BigDecimal.ZERO;
		BigDecimal recd4 = BigDecimal.ZERO;
		BigDecimal recdIEPS = BigDecimal.ZERO;
		
		List<Status02> list4 = new ArrayList<Status02>();
		Status02 issued = new Status02("Issued");
		Status02 cancelled = new Status02("Cancelled");
		Status02 refunded = new Status02("Refunded");
		Status02 writtenOff = new Status02("Written Off");
		list4.add(issued);
		list4.add(cancelled);
		list4.add(refunded);
		list4.add(writtenOff);
		for(Object[] object:list){
			int i=0;
			ReceiptCollected01 rc01 = new ReceiptCollected01();
			String dnNo = (String)object[i++];
			rc01.setDemandNoteNo(dnNo);
			rc01.setAppNo((String)object[i++]); //TODO
			rc01.setIssueDate(formatDate(object[i++]));
			rc01.setDueDate(formatDate(object[i++]));
			String dnStatus = (String)object[i++];
			String receiptStatus = (String)object[i++];
			rc01.setReceiptDate(formatDate(object[i++]));
			rc01.setReceiptNo((String)object[i++]);
			rc01.setBillName((String)object[i++]);
			BigDecimal dnAmount = (BigDecimal)object[i++];
			rc01.setAmount(dnAmount);

			String dnStatusStr = "";
			if(DemandNoteHeader.STATUS_ISSUED.equals(dnStatus)){
				dnStatusStr = "Issued";
				issued.addAmount(dnAmount);
			}else if(DemandNoteHeader.STATUS_WRITTEN_OFF.equals(dnStatus)){
				dnStatusStr = "Written Off";
				writtenOff.addAmount(dnAmount);
			}else if(DemandNoteHeader.STATUS_CANCELLED.equals(dnStatus)){
				dnStatusStr = "Cancelled";
				cancelled.addAmount(dnAmount);
			}else if(DemandNoteHeader.STATUS_REFUNDED.equals(dnStatus)){
				dnStatusStr = "Refunded";
				refunded.addAmount(dnAmount);
			}
			rc01.setDemandNoteStatus(dnStatusStr);

			String receiptStatusStr = "";
			if(DemandNoteHeader.PAYMENT_STATUS_OUTSTANDING.equals(receiptStatus)){
				receiptStatusStr = "Outstanding";
			}else if(DemandNoteHeader.PAYMENT_STATUS_PAID.equals(receiptStatus)){
				receiptStatusStr = "Paid (Full)";
			}else if(DemandNoteHeader.PAYMENT_STATUS_PARTIAL.equals(receiptStatus)){
				receiptStatusStr = "Outstanding (Partial)";
			}else if(DemandNoteHeader.PAYMENT_STATUS_OVERPAID.equals(receiptStatus)){
				receiptStatusStr = "Paid (Overpaid)";
			}

			rc01.setReceiptStatus(receiptStatusStr);
			rc01.setReceiptAmount((BigDecimal)object[i++]);
			rc01.setRefundAmount((BigDecimal)object[i++]);
			rc01.setRefundDate(formatDate(object[i++]));
			String paymentTypeCode = (String)object[i++];
			if(paymentTypeMap.containsKey(paymentTypeCode)){
				rc01.setPaymentType(paymentTypeMap.get(paymentTypeCode));
			}
			String cancelBy=(String)object[i++];
			Object ebsFlag = object[i++];
			if (ebsFlag != null) {
				ebsFlag = ebsFlag.toString();
			}

			rc01.setIsReceiptCancelled(cancelBy!=null?"Y":"N");
			rc01.setFeeCode((String) object[16]);

			int type;
			if (ebsFlag == null) {
				type = 1;
			} else if ("1".equals(ebsFlag)) {
				type = 2;
			} else {
				type = 3;
			}

			switch (type) {
			case 1:
				if (!demandNoteNos.contains(dnNo)) {
					recd1 = recd1.add(dnAmount);
					count1++;
					recd4 = recd4.add(dnAmount);
					count4++;
				}
				rc01.setCount(BigDecimal.valueOf(count1));
				rc01.setReceivedAmount(recd1);
				list1.add(rc01);
				break;
			case 2:
				if (!demandNoteNos.contains(dnNo)) {
					recd2 = recd2.add(dnAmount);
					count2++;
					recd4 = recd4.add(dnAmount);
					count4++;
				}
				rc01.setCount(BigDecimal.valueOf(count2));
				rc01.setReceivedAmount(recd2);
				list2.add(rc01);
				break;
			case 3:
				if (!demandNoteNos.contains(dnNo)) {
					recd3 = recd3.add(dnAmount);
					count3++;
					recd4 = recd4.add(dnAmount);
					count4++;
				}
				rc01.setCount(BigDecimal.valueOf(count3));
				rc01.setReceivedAmount(recd3);
				rc01.setTitle("eBS NON-AUTOPAY");
				list3.add(rc01);
				break;
			}
			demandNoteNos.add(dnNo);
		}
		inputParam.put("demandNoteCase", ""+count4);
		inputParam.put("demandNoteAmount", recd4);

		if (list1.isEmpty()) {
			list1.add(new ReceiptCollected01());
		}
		if (list2.isEmpty()) {
			list2.add(new ReceiptCollected01());
		}
		if (list3.isEmpty()) {
			list3.add(new ReceiptCollected01());
		}

		for (ReceiptCollected01 item:list1) item.setTitle("NON-eBS");
		for (ReceiptCollected01 item:list2) item.setTitle("ebs AUTOPAY");
		for (ReceiptCollected01 item:list3) item.setTitle("eBS NON-AUTOPAY");

		JasperReport subreport1 = jasperReportService.getJasperReport("FIN_Status_01.jrxml");
		inputParam.put("SUBREPORT_1", subreport1);
		inputParam.put("SUBREPORTDS_1", list1);
		inputParam.put("SUBREPORT_2", subreport1);
		inputParam.put("SUBREPORTDS_2", list2);
		inputParam.put("SUBREPORT_3", subreport1);
		inputParam.put("SUBREPORTDS_3", list3);

		JasperReport subreport4 = jasperReportService.getJasperReport("FIN_Status_02.jrxml");
		inputParam.put("SUBREPORT_4", subreport4);
		inputParam.put("SUBREPORTDS_4", list4);
	}

	private String formatDate(Object o){
		try{
			return REPORT_DATE_FORMAT.format((Date)o);
		}catch(Exception ex){
//			ignore
		}
		return null;
	}

}