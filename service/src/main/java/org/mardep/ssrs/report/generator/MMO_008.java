package org.mardep.ssrs.report.generator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mardep.ssrs.domain.codetable.Nationality;
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
 * MMO Report-008: Summary of seafarer Waiting for Employment
 * 
 * @author Leo.LIANG
 *
 */
@Service("RPT_MMO_008")
public class MMO_008 extends AbstractKeyValue implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public String getReportFileName() {
		return "MMO_distribution.jrxml";
	}
	
	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		Long nationalityId = (Long)inputParam.get("nationalityId");
		
		Nationality nationality = nationalityDao.findById(nationalityId);
		String nationalityEngDesc = nationality.getEngDesc();
		logger.info("####### RPT_MMO_008  #########");
		logger.info("Report Date:{}", reportDate);
		logger.info("Nationality, {}-{}", new Object[]{nationalityId, nationalityEngDesc});
		
		List<KeyValue> officerList = ratingDao.countNationalityRank(reportDate, nationalityId, "O").stream().map(o->new KeyValue((String)o[0], ((Integer)o[1]).toString())).collect(Collectors.toList());
		List<KeyValue> ratingList = ratingDao.countNationalityRank(reportDate, nationalityId, "R").stream().map(o->new KeyValue((String)o[0], ((Integer)o[1]).toString())).collect(Collectors.toList());
	
		int officerSum = officerList.stream().mapToInt(kv -> (Integer.valueOf(kv.getValue()))).sum();
		int ratingSum = ratingList.stream().mapToInt(kv -> (Integer.valueOf(kv.getValue()))).sum();

		String reportId = "SRS1110";
		String reportTitle = "Distribution of Crew by Rank / Rating by Nationality";
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		
		String title1 = String.format("NUMBER OF %s OFFICER", nationalityEngDesc);
		String title2 = String.format("NUMBER OF %s RATING", nationalityEngDesc);
		
		
		String summary1 = String.format("TOTAL NO OF CREW:       %s", officerSum);
		String summary2 = String.format("TOTAL NO OF CREW:       %s", ratingSum);
		List<MMO_Distribution> DistributionList = new ArrayList<MMO_Distribution>();
		DistributionList.add(new MMO_Distribution(title1, officerList,  summary1));
		DistributionList.add(new MMO_Distribution(title2, ratingList,  summary2));
		
		JasperReport kvJR = jasperReportService.getJasperReport("KeyValue.jrxml");
		JasperReport mmoPHJR = jasperReportService.getJasperReport("MMO_PrintHorizontal.jrxml");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("printHorizontal", mmoPHJR);
		map.put("keyValue", kvJR);
		map.put(REPORT_ID, reportId);
		map.put(REPORT_TITLE, reportTitle);
		map.put(USER_ID, currentUser!=null?currentUser:"SYSTEM");
		

		return generate(DistributionList, map);
	}
	
}
