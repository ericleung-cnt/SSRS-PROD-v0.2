package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.codetable.IFeeCodeDao;
import org.mardep.ssrs.dao.dn.IDemandNoteReceiptDao;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.bean.ReceiptCollected01;
import org.mardep.ssrs.report.bean.ReceiptCollected02;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * SSRS Receipt Collected Report
 *
 */
@Service("RPT_FIN_006")
public class RPT_FIN_006 extends AbstractFinReport {

	final static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private IDemandNoteReceiptDao demandNoteReceiptDao;

	@Autowired
	private IFeeCodeDao fcDao;

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		final String reportTitle="Receipt Collected Report";
		final String reportSubTitle="Period: %s To %s";
		Date from = (Date)inputParam.get(DATE_FROM);
		Date to = (Date)inputParam.get(DATE_TO);

		inputParam.put(REPORT_ID, "FIN006");
		inputParam.put(USER_ID, UserContextThreadLocalHolder.getCurrentUserId());
		inputParam.put(REPORT_TITLE, reportTitle);
		inputParam.put(REPORT_SUB_TITLE, String.format(reportSubTitle, df.format(from), df.format(to)));


		to = new Date(to.getTime() + 24L*3600 * 1000 - 1);

		String sortBy = (String)inputParam.get(SORT_BY);

//		List<DemandNoteReceipt> collecteds = demandNoteReceiptDao.findCollected(from, to, sortBy);
		List<Object[]> rc01List = demandNoteReceiptDao.findReceiptCollected01(from, to, sortBy);
		fillData01(inputParam, rc01List);
		fillData02(inputParam, rc01List);

		List<String> dummyList = new ArrayList<String>();
		dummyList.add("TEST");

		return generate(dummyList, inputParam);
	}

	private void fillData01(Map<String, Object> inputParam, List<Object[]> rc01List){
		List<ReceiptCollected01> list1 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list2 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list3 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list5 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list6 = new ArrayList<ReceiptCollected01>();
		// for IEPS
		List<ReceiptCollected01> list7 = new ArrayList<ReceiptCollected01>();
		
		String lastDemandNoteNo = null;
		int count1 = 0; // NON-eBS
		int count2 = 0; // eBS AUTOPAY
		int count3 = 0; // eBS NON-AUTOPAY
		int count5 = 0; // eBS PPS
		int count6 = 0; // eBS Credit Card
		// for IEPS
		int count7 = 0; // IEPS
		
		int countTotal = 0;

		BigDecimal cancel1 = BigDecimal.ZERO;
		BigDecimal cancel2 = BigDecimal.ZERO;
		BigDecimal cancel3 = BigDecimal.ZERO;
		BigDecimal cancel5 = BigDecimal.ZERO;
		BigDecimal cancel6 = BigDecimal.ZERO;
		// for IEPS
		BigDecimal cancel7 = BigDecimal.ZERO;
		
		BigDecimal cancelTotal = BigDecimal.ZERO;
		
		
		BigDecimal recd1 = BigDecimal.ZERO;
		BigDecimal recd2 = BigDecimal.ZERO;
		BigDecimal recd3 = BigDecimal.ZERO;
		BigDecimal recd5 = BigDecimal.ZERO;
		BigDecimal recd6 = BigDecimal.ZERO;
		// for IEPS
		BigDecimal recd7 = BigDecimal.ZERO;
		
		BigDecimal recdTotal = BigDecimal.ZERO;

		HashSet<String> receipts = new HashSet<>();
		for(Object[] r:rc01List){
			if (receipts.contains(r[5])) {
				continue;
			} else {
				receipts.add((String) r[5]);
			}
			ReceiptCollected01 rc01 = new ReceiptCollected01();
			String demandNoteNo = (String) r[0];
			if(!demandNoteNo.equalsIgnoreCase(lastDemandNoteNo)){
				lastDemandNoteNo = demandNoteNo;
				//make it blank if same demandNoteNo
				rc01.setDemandNoteNo(demandNoteNo);
				rc01.setAmount((BigDecimal) r[7]);
				rc01.setBillName((String) r[6]);
			}
			Date issueDate=(Date) r[1];
			if(issueDate!=null){
				rc01.setIssueDate(REPORT_DATE_FORMAT.format(issueDate));
			}
			Date dueDate=(Date) r[2];
			if(dueDate!=null){
				rc01.setDueDate(REPORT_DATE_FORMAT.format(dueDate));
			}
			DemandNoteHeader dnStatus = new DemandNoteHeader();
			dnStatus.setPaymentStatus((String) r[3]);
			rc01.setReceiptStatus(dnStatus.getPaymentStatusStr());
			rc01.setReceiptDate(REPORT_DATE_FORMAT.format(r[4]));
			rc01.setReceiptNo((String) r[5]);
			BigDecimal receiptAmount = (BigDecimal) r[8];
			//if(DemandNoteHeader.STATUS_CANCELLED.equals(demandNote.getStatus())){
			if ("Y".equals(r[9])) {
				cancelTotal = cancelTotal.add(receiptAmount);
				if (r[11]==null && "95".equals(String.valueOf(r[11]))) {
					cancel7 = cancel7.add(receiptAmount);
				} else if (r[11] == null) {
					cancel1 = cancel1.add(receiptAmount);
				} else if ("1".equals(String.valueOf(r[11]))) {
					cancel2 = cancel2.add(receiptAmount);
				} else if("6".equals(String.valueOf(r[11]))){
					cancel5 = cancel5.add(receiptAmount);
				} else if("7".equals(String.valueOf(r[11])) || "8".equals(String.valueOf(r[11]))){
					cancel6 = cancel6.add(receiptAmount);
				} else {
					cancel3 = cancel3.add(receiptAmount);
				}
				rc01.setIsReceiptCancelled("Y");
			}else{
				recdTotal = recdTotal.add(receiptAmount);
				if (r[11]==null && "95".equals(String.valueOf(r[15]))) {
					recd7 = recd7.add(receiptAmount);
					count7++;
				} else if (r[11] == null) {
					recd1 = recd1.add(receiptAmount);
					count1++;
				} else if ("1".equals(String.valueOf(r[11]))) {
					recd2 = recd2.add(receiptAmount);
					count2++;
				} else if ("0".equals(String.valueOf(r[11])) && "90".equals(String.valueOf(r[15])) ) {
					recd5 = recd5.add(receiptAmount);
					count5++;
				} else if ("0".equals(String.valueOf(r[11])) && "50".equals(String.valueOf(r[15])) ) {
					recd6 = recd6.add(receiptAmount);
					count6++;
				} else {
					recd3 = recd3.add(receiptAmount);
					count3++;
				}
				rc01.setIsReceiptCancelled("N");
				countTotal++;
			}
//			6-PPS
//			7/8 -Credit Card
			rc01.setReceiptAmount(receiptAmount);
			rc01.setFeeCode((String) r[10]);
			if (r[11] == null && "95".equals(String.valueOf(r[15])) ) {
				list7.add(rc01);
				rc01.setCount(BigDecimal.valueOf(count7));
				rc01.setCancelledAmount(cancel7);
				rc01.setReceivedAmount(recd7);
				rc01.setTitle("IEPS");
			} else if (r[11] == null) {
				list1.add(rc01);
				rc01.setCount(BigDecimal.valueOf(count1));
				rc01.setCancelledAmount(cancel1);
				rc01.setReceivedAmount(recd1);
				rc01.setTitle("NON-eBS");
			} else if ("1".equals(String.valueOf(r[11]))) {
				list2.add(rc01);
				rc01.setCount(BigDecimal.valueOf(count2));
				rc01.setCancelledAmount(cancel2);
				rc01.setReceivedAmount(recd2);
				rc01.setTitle("eBS AUTOPAY");
			} else if ("0".equals(String.valueOf(r[11])) && "90".equals(String.valueOf(r[15])) ) {
				list5.add(rc01);
				rc01.setCount(BigDecimal.valueOf(count5));
				rc01.setCancelledAmount(cancel5);
				rc01.setReceivedAmount(recd5);
				rc01.setTitle("eBS PPS");
			} else if ("0".equals(String.valueOf(r[11])) && "50".equals(String.valueOf(r[15])) ) {
				list6.add(rc01);
				rc01.setCount(BigDecimal.valueOf(count6));
				rc01.setCancelledAmount(cancel6);
				rc01.setReceivedAmount(recd6);
				rc01.setTitle("eBS Credit Card");
			} else {
				list3.add(rc01);
				rc01.setCount(BigDecimal.valueOf(count3));
				rc01.setCancelledAmount(cancel3);
				rc01.setReceivedAmount(recd3);
				rc01.setTitle("eBS NON-AUTOPAY");
			}
		}
		inputParam.put("noOfRecords", countTotal);
		inputParam.put("receivedAmount", recdTotal);
		inputParam.put("cancelledAmount", cancelTotal);

		if (list1.isEmpty()) {
			list1.add(new ReceiptCollected01());
		}
		if (list2.isEmpty()) {
			list2.add(new ReceiptCollected01());
		}
		if (list3.isEmpty()) {
			list3.add(new ReceiptCollected01());
		}
		if (list5.isEmpty()) {
			list5.add(new ReceiptCollected01());
		}
		if (list6.isEmpty()) {
			list6.add(new ReceiptCollected01());
		}
		if (list7.isEmpty()) {
			list7.add(new ReceiptCollected01());
		}

		list1.get(0).setTitle("NON-eBS");
		list2.get(0).setTitle("eBs AUTOPAY");
		list3.get(0).setTitle("eBS NON-AUTOPAY");
		
		list5.get(0).setTitle("eBS PPS");
		list6.get(0).setTitle("eBS Credit Card");
		list7.get(0).setTitle("IEPS");
		
		JasperReport subreport1 = jasperReportService.getJasperReport("FIN_ReceiptCollected_01.jrxml");
		inputParam.put("SUBREPORT_1", subreport1);
		inputParam.put("SUBREPORTDS_1", list1);
		inputParam.put("SUBREPORT_2", subreport1);
		inputParam.put("SUBREPORTDS_2", list2);
		inputParam.put("SUBREPORT_3", subreport1);
		inputParam.put("SUBREPORTDS_3", list3);

		inputParam.put("SUBREPORT_5", subreport1);
		inputParam.put("SUBREPORTDS_5", list5);
		inputParam.put("SUBREPORT_6", subreport1);
		inputParam.put("SUBREPORTDS_6", list6);
		
		inputParam.put("SUBREPORT_7", subreport1);
		inputParam.put("SUBREPORTDS_7", list7);
	}

	private void fillData02(Map<String, Object> inputParam, List<Object[]> rc01List){
		Map<String, ReceiptCollected02> map = new HashMap<>();
		for (Object[] r : rc01List) {
			if (DemandNoteHeader.PAYMENT_STATUS_OUTSTANDING.equals(r[3])) {
				continue; // skip payment status == outstanding
			}
			if ("Y".equals(r[9])) {
				continue;
			}
			ReceiptCollected02 receiptCollected02 = map.get(r[12]);
			if (receiptCollected02 == null) {
				FeeCode code = fcDao.findById((String) r[12]);
				receiptCollected02 = new ReceiptCollected02();
				receiptCollected02.setDescription(code.getEngDesc());
				receiptCollected02.setFeeCode(code.getFormCode());
				receiptCollected02.setItemCode(code.getId());
				receiptCollected02.setPrice(code.getFeePrice());
				receiptCollected02.setAmount(BigDecimal.ZERO);
				receiptCollected02.setChargedUnit(0);
				map.put((String) r[12], receiptCollected02);
			}
			receiptCollected02.setAmount(receiptCollected02.getAmount().add((BigDecimal) r[14]));
			receiptCollected02.setChargedUnit(receiptCollected02.getChargedUnit() + (Integer) r[13]);
		}
		for (ReceiptCollected02 r : map.values()) {
			if (r.getChargedUnit() == 0 || !r.getAmount().divide(new BigDecimal(r.getChargedUnit()), 2, RoundingMode.HALF_UP).equals(r.price)) {
				r.setPrice(null);
			}
		}
		JasperReport subreport2 = jasperReportService.getJasperReport("FIN_ReceiptCollected_02.jrxml");
		inputParam.put("SUBREPORT_4", subreport2);
		List<ReceiptCollected02> list = new ArrayList<ReceiptCollected02>(map.values());
		Collections.sort(list);
		inputParam.put("SUBREPORTDS_4", list);
	}
}
