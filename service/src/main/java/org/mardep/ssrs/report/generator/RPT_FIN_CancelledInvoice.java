package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.bean.ReceiptCollected01;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * SSRS Fin Cancelled/Written-off Invoice Report
 *  <li>ReceiptNo
 *  <li>ReceiptDate
 *  <li>ReceiptAmount
 *  <li>ReceiptStatus
 *  <li>UserName
 *  <li>Remarks
 *
 */
@Service("RPT_FIN_CANCELLED")
public class RPT_FIN_CancelledInvoice extends AbstractFinReport {

	public final static DateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");

	@Autowired
	private IDemandNoteHeaderDao demandNoteHeaderDao;

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		final String reportTitle="CANCELLED / WRITTEN-OFF Demand Note Report";
		inputParam.put(REPORT_TITLE, reportTitle);
		inputParam.put(REPORT_ID, "RD0080");
		inputParam.put(USER_ID, UserContextThreadLocalHolder.getCurrentUserName());

		fillData01(inputParam);

		List<String> dummyList = new ArrayList<String>();
		dummyList.add("TEST");

		return generate(dummyList, inputParam);
	}

	private void fillData01(Map<String, Object> inputParam) throws Exception{
		Date from = (Date)inputParam.get(DATE_FROM);
		Date to = (Date)inputParam.get(DATE_TO);
		final String reportSubTitle="From %s To %s";
		inputParam.put(REPORT_SUB_TITLE, String.format(reportSubTitle, TITLE_DATE_FORMAT.format(from), TITLE_DATE_FORMAT.format(to)));

		Integer receivedAmount = 0;
		BigDecimal demandNoteAmount = BigDecimal.ZERO;

//		Null non ebs
//		1 ebs autopay,
//		Others ebs non autopay

		to = new Date(to.getTime() + 24L*3600 * 1000 - 1);
		Map<String, List<DemandNoteHeader>> map = new HashMap<>();
		List<DemandNoteHeader> dnList1 = demandNoteHeaderDao.findCancelled(from, to, null);
		List<DemandNoteHeader> dnList2 = demandNoteHeaderDao.findCancelled(from, to, "1");
		List<DemandNoteHeader> dnList3 = demandNoteHeaderDao.findCancelled(from, to, "2");

		map.put("SUBREPORTDS_1", dnList1);
		map.put("SUBREPORTDS_2", dnList2);
		map.put("SUBREPORTDS_3", dnList3);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		List<ReceiptCollected01> dummyList = new ArrayList<ReceiptCollected01>();
		dummyList.add(new ReceiptCollected01());

		for(Entry<String, List<DemandNoteHeader>> e:map.entrySet()){
			String key = e.getKey();
			List<DemandNoteHeader> dnList = e.getValue();
			List<ReceiptCollected01> list1 = new ArrayList<>();
			for(DemandNoteHeader demandNote:dnList){
				ReceiptCollected01 er = new ReceiptCollected01();
				BigDecimal dnAmount = demandNote.getAmount();
				if(dnAmount!=null){
					demandNoteAmount = demandNoteAmount.add(dnAmount);
				}
				er.setReceiptNo(demandNote.getDemandNoteNo());
				er.setReceiptDate(REPORT_DATE_FORMAT.format(demandNote.getGenerationTime()));
				er.setReceiptAmount(dnAmount);
				er.setReceiptStatus(demandNote.getStatusStr());
				er.setUserName(demandNote.getCwBy());
				if(demandNote.getCwTime()!=null){
					er.setUpdateTime(simpleDateFormat.format(demandNote.getCwTime()));
				}
				er.setRemarks(demandNote.getCwRemark());
				list1.add(er);
			}
			if(list1.size()==0){
				inputParam.put(key, dummyList);
			}else{
				inputParam.put(key, list1);
			}
		}
		JasperReport subreport1 = jasperReportService.getJasperReport("FIN_CancelledInvoice_01.jrxml");
		subreport1.setProperty("01_title", "NON-eBS");
		JasperReport subreport2 = jasperReportService.getJasperReport("FIN_CancelledInvoice_02.jrxml");
		subreport2.setProperty("01_title", "eBS AUTOPAY");
		JasperReport subreport3 = jasperReportService.getJasperReport("FIN_CancelledInvoice_03.jrxml");
		subreport3.setProperty("01_title", "eBS NON-AUTOPAY");
		inputParam.put("SUBREPORT_1", subreport1);
		inputParam.put("SUBREPORT_2", subreport2);
		inputParam.put("SUBREPORT_3", subreport3);
		JasperReport subreport4 = jasperReportService.getJasperReport("FIN_CancelledInvoice_04.jrxml");
		inputParam.put("SUBREPORT_4", subreport4);
		List<String> dummyList4 = new ArrayList<String>();
		dummyList4.add("TEST");
		inputParam.put("SUBREPORTDS_4", dummyList4);

		inputParam.put("demandNoteRetrieved", receivedAmount+dnList1.size()+dnList2.size()+dnList3.size());
		inputParam.put("demandNoteAmount", demandNoteAmount);
	}

}