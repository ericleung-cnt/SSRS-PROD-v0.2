package org.mardep.ssrs.report.generator;

import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mardep.ssrs.dao.codetable.IRankDao;
import org.mardep.ssrs.dao.seafarer.IRatingDao;
import org.mardep.ssrs.report.bean.TenYearRangeAge;
import org.mardep.ssrs.report.bean.TenYearRangeRank;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * for Report 002/003/006 in MMO
 * 
 * @author Leo.LIANG
 *
 */
public abstract class AbstractTenYearRange extends AbstractReportGenerator{

	@Autowired
	protected IRankDao rankDao;
	
	@Autowired
	protected IRatingDao ratingDao;
	
	@Override
	public String getReportFileName() {
		return "MMO_TenYearRange_Summary.jrxml";
	}
	
	protected List<TenYearRangeRank> initResultList(){
		 Comparator<TenYearRangeRank> comparator = Comparator.comparing(TenYearRangeRank::getDepartment);
		 comparator = comparator.thenComparing(Comparator.comparing(TenYearRangeRank::getRank));
		 List<TenYearRangeRank> resultList= rankDao.findAll().stream().map(s->new TenYearRangeRank(s.getDepartment(), s.getEngDesc())).collect(Collectors.toList());
		 resultList.sort(comparator);
		 return resultList;
	}

	protected abstract Map<String, Object> getMap(Map<String, Object> inputParam);
	
	protected abstract Map<String, Long> getResultMap(Map<String, Object> inputParam, TenYearRangeAge dateAge);
		
	
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		String partType = (String)inputParam.get("partType");
		logger.info("Report Date:{}", reportDate);
		logger.info("Part Type:{}", partType);
		
		Map<String, Object> reportParaMap = getMap(inputParam);
		// Data set from Table rank;
		List<TenYearRangeRank> set = initResultList();
		for (TenYearRangeAge age : EnumSet.allOf(TenYearRangeAge.class)) {
			TenYearRangeAge dateAge = age.instance(reportDate);
			Map<String, Long> ageMap = getResultMap(inputParam, dateAge);
			for (TenYearRangeRank rank : set) {
				String rankName = rank.getRank();
				if (ageMap!=null && ageMap.containsKey(rankName)) {
					Long rankCount = ageMap.get(rankName);

					switch (age) {
					case _20:
						rank.age20Add(rankCount);
						break;
					case _21_TO_30:
						rank.age2130Add(rankCount);
						break;
					case _31_TO_40:
						rank.age3140Add(rankCount);
						break;
					case _41_TO_50:
						rank.age4150Add(rankCount);
						break;
					case _51_TO_60:
						rank.age5160Add(rankCount);
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
		return generate(set, reportParaMap);
	}
}
