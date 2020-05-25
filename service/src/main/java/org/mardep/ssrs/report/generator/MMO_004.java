package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mardep.ssrs.dao.seafarer.IRatingDao;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.report.bean.FiveYearRangeAge;
import org.mardep.ssrs.report.bean.FiveYearRangeRank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Report-004: Summary of Registration of HK Registered Seafarer
 * 
 * @author Leo.LIANG
 *
 */
@Service("RPT_MMO_004")
public class MMO_004 extends AbstractAgeRange implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	IRatingDao ratingDao;
	
	@Override
	public String getReportFileName() {
		return "MMO_004_Summary_of_Registration_of_Hong_Kong_Seafarer.jrxml";
	}
	
	protected List<FiveYearRangeRank> initResultList(){
		//TODO how to check part type?
		Comparator<FiveYearRangeRank> comparator = Comparator.comparing(FiveYearRangeRank::getDepartment);
	    comparator = comparator.thenComparing(Comparator.comparing(FiveYearRangeRank::getRank));
	    List<FiveYearRangeRank> resultList= rankDao.findAll().stream().map(s->new FiveYearRangeRank(s.getDepartment(), s.getEngDesc())).collect(Collectors.toList());
	    resultList.sort(comparator);
		return resultList;
	}
	
	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		String partType = (String)inputParam.get("partType");
		logger.info("####### RPT_MMO_004  #########");
		logger.info("Report Date:{}", reportDate);
		logger.info("Part Type:{}", partType);
		
		String reportId = "SRS2070";
		String partTypeDisplay = partType==null?"1 & 2":partType;
		String reportDateDisplay = new SimpleDateFormat("yyyy/MM/dd").format(reportDate);
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(REPORT_ID, reportId);
		map.put(USER_ID, currentUser!=null?currentUser:"SYSTEM");
		map.put(REPORT_DATE, reportDateDisplay);
		map.put(PART, partTypeDisplay);
		
		// Data set from Table rank;
		List<FiveYearRangeRank> set = initResultList();
		for(FiveYearRangeAge age:EnumSet.allOf(FiveYearRangeAge.class)){
			FiveYearRangeAge dateAge = age.instance(reportDate);
			Map<String, Long> ageMap = ratingDao.countAge(reportDate, dateAge.getFrom(), dateAge.getTo(), partType);
			
			for(FiveYearRangeRank rank:set){
				String rankName = rank.getRank();
				if(ageMap.containsKey(rankName)){
					Long rankCount = ageMap.get(rankName);
					
					switch(age){
					case _20:
						rank.age20Add(rankCount);
						break;
					case _21_TO_25:
						rank.age2125Add(rankCount);
						break;
					case _26_TO_30:
						rank.age2630Add(rankCount);
						break;
					case _31_TO_35:
						rank.age3135Add(rankCount);
						break;
					case _36_TO_40:
						rank.age3640Add(rankCount);
						break;
					case _41_TO_45:
						rank.age4145Add(rankCount);
						break;
					case _46_TO_50:
						rank.age4650Add(rankCount);
						break;
					case _51_TO_55:
						rank.age5155Add(rankCount);
						break;
					case _56_TO_60:
						rank.age5660Add(rankCount);
						break;
					case _61:
						rank.age61Add(rankCount);
						break;
					default:
						break;
					}
				}
			}
		}
		
		return generate(set, map);
	}
	
}
