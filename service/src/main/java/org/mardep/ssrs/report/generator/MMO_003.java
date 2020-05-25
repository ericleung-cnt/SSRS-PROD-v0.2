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
 * MMO Report-003: No. of Seafarers (Part 1) Listed with Permitted Company
 *
 * @author Leo.LIANG
 *
 */
@Service("RPT_MMO_003")
public class MMO_003 extends AbstractTenYearRange implements IReportGenerator{

	@Override
	protected Map<String, Object> getMap(Map<String, Object> inputParam){
		Date reportDate = (Date)inputParam.get("reportDate");
		String partType = (String)inputParam.get("partType");
		String companyName = "";// TODO
		logger.info("####### RPT_MMO_003  #########");
		logger.info("Report Date:{}", reportDate);
		logger.info("Part Type:{}", partType);

		String reportId = "SRS2090";
		String partTypeDisplay = partType==null?"1 & 2":partType;
		String reportDateDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(reportDate);
		String title = "Summary Report - Data as at "+reportDateDisplay;
		String subTitle = "No. of Seafarers (Part "+partTypeDisplay+") Listed with Permitted Company";
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();

		Map<String, Object> resutlMap = new HashMap<String, Object>();
		resutlMap.put(REPORT_ID, reportId);
		resutlMap.put(USER_ID, currentUser!=null?currentUser:"SYSTEM");
		resutlMap.put(REPORT_TITLE, title);
		resutlMap.put(REPORT_SUB_TITLE, subTitle);
		resutlMap.put(COMPANY_NAME, companyName);
		return resutlMap;
	}

	@Override
	protected Map<String, Long> getResultMap(Map<String, Object> inputParam, TenYearRangeAge dateAge){
		String partType = (String)inputParam.get("partType");
		String companyName = (String)inputParam.get("companyName");
		Map<String, Long> ageMap = ratingDao.countCompany(dateAge.getFrom(), dateAge.getTo(), companyName, partType);
		return ageMap;
	}

}
