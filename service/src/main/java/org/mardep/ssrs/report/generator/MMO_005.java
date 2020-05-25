package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.mardep.ssrs.dao.seafarer.IRatingDao;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.report.bean.AverageWage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * MMO Report-005: Summary of Average Wages of HK Registered Seafarer
 *
 * @author Leo.LIANG
 *
 */
@Service("RPT_MMO_005")
public class MMO_005 extends AbstractAgeRange implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IRatingDao ratingDao;

	@Override
	public String getReportFileName() {
		return "MMO_005_Summary_of_Average_Wages.jrxml";
	}

	protected List<AverageWage> initResultList(){
		return rankDao.findAll().stream().map(s->new AverageWage(s.getDepartment(), s.getEngDesc())).collect(Collectors.toList());
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		String partType = (String)inputParam.get("partType");
		logger.info("####### RPT_MMO_005  #########");
		logger.info("Report Date:{}", reportDate);
		logger.info("Part Type:{}", partType);

		String reportId = "SRS2080";
		String partTypeDisplay = partType==null?"1 & 2":partType;
		String reportDateDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(reportDate);
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(REPORT_ID, reportId);
		map.put(REPORT_DATE, reportDateDisplay);
		map.put(PART, partTypeDisplay);
		map.put(USER_ID, currentUser!=null?currentUser:"SYSTEM");

		List<AverageWage> result = initResultList();
		List<Object[]> resultList = ratingDao.countAverageWage(reportDate, partType);
		for(Object[] o:resultList){
			String rank = (String)o[1];
			for(AverageWage aw:result){
				if(aw.getRank().equals(rank)){
					aw.setNoOfSeafarer((Integer)o[2]);
					BigDecimal totalWage = (BigDecimal)o[3];
					if(totalWage!=null){
						aw.setTotalWage(totalWage);
					}
					break;
				}
			}
		}

		return generate(result, map);
	}

}
