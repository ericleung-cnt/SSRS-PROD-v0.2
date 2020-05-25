package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.report.bean.TenYearRangeAge;
import org.springframework.stereotype.Service;

/**
 *
 * MMO Report-006: Summary of seafarer Waiting for Employment
 *
 * @author Leo.LIANG
 *
 */
@Service("RPT_MMO_006")
public class MMO_006 extends AbstractTenYearRange implements IReportGenerator{

	@Override
	protected Map<String, Object> getMap(Map<String, Object> inputParam){
		Date reportDate = (Date)inputParam.get("reportDate");
		String partType = (String)inputParam.get("partType");
		logger.info("####### RPT_MMO_006  #########");

		String reportId = "SRS2090";
		String partTypeDisplay = partType==null?"1 & 2":partType;
		String reportDateDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(reportDate);
		String title = "Summary of Seafarer Waiting for Employment - Data as at "+reportDateDisplay;
		String subTitle = "Breakdown of Seafarer (Part "+partTypeDisplay+") by age";
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();

		Map<String, Object> resutlMap = new HashMap<String, Object>();
		resutlMap.put(REPORT_ID, reportId);
		resutlMap.put(USER_ID, currentUser!=null?currentUser:"SYSTEM");
		resutlMap.put(REPORT_TITLE, title);
		resutlMap.put(REPORT_SUB_TITLE, subTitle);
		resutlMap.put(COMPANY_NAME, "");
		return resutlMap;
	}

	@Override
	protected Map<String, Long> getResultMap(Map<String, Object> inputParam, TenYearRangeAge dateAge){
		String partType = (String)inputParam.get("partType");
		Map<String, Long> ageMap = ratingDao.countAgeWaitingEmployment(dateAge.getFrom(), dateAge.getTo(), partType);
		return ageMap;
	}

}
