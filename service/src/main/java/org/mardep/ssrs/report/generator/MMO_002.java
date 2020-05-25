package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.report.bean.TenYearRangeAge;
import org.mardep.ssrs.report.bean.TenYearRangeRank;
import org.springframework.stereotype.Service;

/**
 *
 * MMO Report-002: No. of Seafarers (Part 1) Now Serving on Board
 *
 * @author Leo.LIANG
 *
 */
@Service("RPT_MMO_002")
public class MMO_002 extends AbstractTenYearRange implements IReportGenerator{

	@Override
	protected Map<String, Object> getMap(Map<String, Object> inputParam){
		throw new UnsupportedOperationException();
	}

	@Override
	protected Map<String, Long> getResultMap(Map<String, Object> inputParam, TenYearRangeAge dateAge){
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		String partType = (String)inputParam.get("partType");
		String reportType = (String)inputParam.get("reportType");
		String reportId = "SRS2060";
		String partTypeDisplay = partType==null?"1 & 2":partType;
		String reportDateDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(reportDate);
		String title = "Summary Report - Data as at "+reportDateDisplay;

		String subTitle = "No. of Seafarers (Part "+partTypeDisplay+") ";
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();

		switch (reportType) {
		case "1": //
			subTitle += " Now Serving on Board";
			break;
		case "2": //
			subTitle += " Listed with Permitted Company";
			break;
		case "3": //
			subTitle += " Onboard over 12 months";
			break;
		case "4": //
			subTitle += " Discharge less than 12 months";
			break;
		case "5": //
			subTitle += " Discharge 12 to 24 months";
			break;
		case "6": //
			subTitle += " Discharge over 24 months";
			break;
		}

		Map<String, Object> resutlMap = inputParam;
		resutlMap.put(REPORT_ID, reportId);
		resutlMap.put(USER_ID, currentUser!=null?currentUser:"SYSTEM");
		resutlMap.put(REPORT_TITLE, title);
		resutlMap.put(REPORT_SUB_TITLE, subTitle);
		resutlMap.put(COMPANY_NAME, "");

		List<TenYearRangeRank> list = initResultList();
		// rank, agegroup 1-6
		Map<String, Object[]> rows = ratingDao.getEmploymentSituation(reportDate, partType, reportType);
		for (TenYearRangeRank r : list) {
			Object[] objects = rows.get(r.getRank());
			if (objects != null) {
				r.age20Add((Integer)objects[0]);
				r.age2130Add((Integer)objects[1]);
				r.age3140Add((Integer)objects[2]);
				r.age4150Add((Integer)objects[3]);
				r.age5160Add((Integer)objects[4]);
				r.age61Add((Integer)objects[5]);
			}
		}
		return super.generate(list, resutlMap);
	}

}
