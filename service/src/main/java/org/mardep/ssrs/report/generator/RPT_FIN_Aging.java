package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.mardep.ssrs.dao.codetable.ISystemParamDao;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.bean.Aging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * SSRS Fin Aging Report
 *  <li>DemandNoteNo
 *  <li>IssueDate
 *  <li>DueDate
 *  <li>Day21
 *  <li>Day41
 *  <li>Day90
 *  <li>OverDay90
 *  <li>1st Rem Date
 *  <li>1nd Rem Date
 *
 */
@Service("RPT_FIN_AGING")
public class RPT_FIN_Aging extends AbstractFinReport {

	public final static DateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

	@Autowired
	private IDemandNoteHeaderDao demandNoteHeaderDao;

	@Autowired
	private ISystemParamDao systemParamDao;

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		final String reportSubTitle="";
		inputParam.put(REPORT_ID, "FINXXX");
		inputParam.put(USER_ID, UserContextThreadLocalHolder.getCurrentUserName());
		inputParam.put(REPORT_SUB_TITLE, reportSubTitle);

		fillData01(inputParam);

		List<String> dummyList = new ArrayList<String>();
		dummyList.add("TEST");

		return generate(dummyList, inputParam);
	}

	private void fillData01(Map<String, Object> inputParam){
		String sortBy = (String)inputParam.get("sortBy");
		String overDueTimeFrame = (String)inputParam.get("overDueTimeFrame");
		Date dateOn = (Date)inputParam.get(DATE_ON);
		final String reportTitle="Account Receivable Aging Report on %s";
		inputParam.put(REPORT_TITLE, String.format(reportTitle, TITLE_DATE_FORMAT.format(dateOn)));

		BigDecimal totalDemandNoteAmount = BigDecimal.ZERO;
		//logic:find outstanding demand note;
		List<DemandNoteHeader> demandNoteList = demandNoteHeaderDao.findAge(sortBy, overDueTimeFrame!=null?Integer.valueOf(overDueTimeFrame):null, dateOn);

		Date current = new Date();

		List<Aging> list1 = new ArrayList<Aging>();
		List<Aging> list2 = new ArrayList<Aging>();
		List<Aging> list3 = new ArrayList<Aging>();
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		BigDecimal recd1 = BigDecimal.ZERO;
		BigDecimal recd2 = BigDecimal.ZERO;
		BigDecimal recd3 = BigDecimal.ZERO;
		BigDecimal recd4 = BigDecimal.ZERO;

		for(DemandNoteHeader demandNote:demandNoteList){
			int type;
			if (demandNote.getEbsFlag() == null) {
				type = 1;
			} else if ("1".equals(demandNote.getEbsFlag())) {
				type = 2;
			} else {
				type = 3;
			}
			Aging aging = new Aging();
			aging.setDemandNoteNo(demandNote.getDemandNoteNo());
			BigDecimal amount = demandNote.getAmount();
			totalDemandNoteAmount = totalDemandNoteAmount.add(amount);
			aging.setAmount(amount);
			Date issueDate = demandNote.getGenerationTime();
			Date dueDate = demandNote.getDueDate();
			aging.setIssueDate(REPORT_DATE_FORMAT.format(issueDate));
			aging.setDueDate(REPORT_DATE_FORMAT.format(dueDate));
//			DateUtils. // TODO compare date here, assume due date not null;
			long diff = dateOn.getTime() - dueDate.getTime();
		    long dateDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			if(dateDiff>90){
//			>90
				aging.setDayOver90(amount);
			}else if(dateDiff>60){
//			>42
				aging.setDay90(amount);
			}else if(dateDiff>30){
//			>22
				aging.setDay41(amount);
			}else if(dateDiff>=0){
//			>0
				aging.setDay21(amount);
			}
			//TODO retrieve date from system parameter
			Date fmd = demandNote.getFirstReminderDate();
			Date smd = demandNote.getSecondReminderDate();
			if(fmd!=null){
				aging.setFirstRemDate(REPORT_DATE_FORMAT.format(fmd));
			}
			if(smd!=null){
				aging.setSecondRemDate(REPORT_DATE_FORMAT.format(smd));
			}
			BigDecimal demandNoteAmount = demandNote.getAmount();
			recd4 =recd4.add(demandNoteAmount);
			switch (type) {
			case 1:
				recd1 = recd1.add(demandNoteAmount);
				count1++;
				aging.setCount(BigDecimal.valueOf(count1));
				aging.setReceivedAmount(recd1);
				aging.setTitle("NON-eBS");
				list1.add(aging);
				break;
			case 2:
				recd2 = recd2.add(demandNoteAmount);
				count2++;
				// use cancel amount for summary of demand note
				aging.setCount(BigDecimal.valueOf(count2));
				aging.setReceivedAmount(recd2);
				aging.setTitle("eBS AUTOPAY");
				list2.add(aging);
				break;
			case 3:
				recd3 = recd3.add(demandNoteAmount);
				count3++;
				aging.setCount(BigDecimal.valueOf(count3));
				aging.setReceivedAmount(recd3);
				aging.setTitle("eBS NON-AUTOPAY");
				list3.add(aging);
				break;
			}
		}
		inputParam.put("demandNoteRetrieved", demandNoteList.size());
		inputParam.put("demandNoteAmount", totalDemandNoteAmount);

		if (list1.isEmpty()) {
			list1.add(new Aging());
		}
		if (list2.isEmpty()) {
			list2.add(new Aging());
		}
		if (list3.isEmpty()) {
			list3.add(new Aging());
		}

		list1.get(0).setTitle("NON-eBS");
		list2.get(0).setTitle("eBS AUTOPAY");
		list3.get(0).setTitle("eBS NON-AUTOPAY");

		JasperReport subreport1 = jasperReportService.getJasperReport("FIN_Aging_01.jrxml");
		inputParam.put("SUBREPORT_1", subreport1);
		inputParam.put("SUBREPORTDS_1", list1);
		inputParam.put("SUBREPORT_2", subreport1);
		inputParam.put("SUBREPORTDS_2", list2);
		inputParam.put("SUBREPORT_3", subreport1);
		inputParam.put("SUBREPORTDS_3", list3);
	}

}