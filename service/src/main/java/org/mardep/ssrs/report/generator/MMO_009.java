package org.mardep.ssrs.report.generator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.report.bean.KeyValue;
import org.mardep.ssrs.report.bean.MMO_Distribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * MMO Report-009: Summary of seafarer Waiting for Employment
 *
 * @author Leo.LIANG
 *
 */
@Service("RPT_MMO_009")
public class MMO_009 extends AbstractKeyValue implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public String getReportFileName() {
		return "MMO_distribution.jrxml";
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		String rankingRating = (String)inputParam.get("rankingRating");

		logger.info("####### RPT_MMO_009  #########");
		logger.info("Report Date:{}", reportDate);
		logger.info("RankingRating, {}-{}", new Object[]{rankingRating});

		DecimalFormat format = new DecimalFormat("$#,###");
		List<KeyValue> resultList = ratingDao.sumSalaryByRank(reportDate, rankingRating).stream().map(
				o-> {
						return new KeyValue((String)o[0], format.format(o[1]));
					}
				).collect(Collectors.toList());


		String reportId = "SRS1120";
		String reportTitle = "Average Monthly Wages by Rank";
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();

		String title = "";
		if(rankingRating.equalsIgnoreCase("O")){
			title = "OFFICER";
		}else if(rankingRating.equalsIgnoreCase("R")){
			title = "RATING";
		}

		List<MMO_Distribution> distributionList = new ArrayList<MMO_Distribution>();
		distributionList.add(new MMO_Distribution(title, resultList,  ""));

		JasperReport kvJR = jasperReportService.getJasperReport("KeyValue.jrxml");
		JasperReport mmoPHJR = jasperReportService.getJasperReport("MMO_PrintHorizontal.jrxml");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("printHorizontal", mmoPHJR);
		map.put("keyValue", kvJR);
		map.put(REPORT_ID, reportId);
		map.put(REPORT_TITLE, reportTitle);
		map.put(USER_ID, currentUser!=null?currentUser:"SYSTEM");


		return generate(distributionList, map);
	}

}
