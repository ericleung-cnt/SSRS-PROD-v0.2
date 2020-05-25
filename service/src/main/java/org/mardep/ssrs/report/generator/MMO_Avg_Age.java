package org.mardep.ssrs.report.generator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.codetable.IRankDao;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.service.IJasperReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * Average Ages of Crew by Rank by Nationality
 *
 */
@Service("MMO_Avg_Age")
public class MMO_Avg_Age implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IRankDao rankDao;

	@Autowired
	IJasperReportService jasper;

	@Override
	public String getReportFileName() {
		return "MMO_Avg_Age.jrxml";
	}


	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		Long nationality = (Long)inputParam.get("nationality");

		List<Object> rows = rankDao.getAverageAgeByNationality(reportDate, nationality);

		List<HashMap<String, Object>> results = new ArrayList<HashMap<String,Object>>();
		Object lastRating = null;
		for (int i = 0; i < rows.size(); i+= 7) {
			Object rating = ((Object[]) rows.get(i))[3];
			lastRating = rating;
			HashMap<String, Object> result = new HashMap<String, Object>();
			result.put("nationality", ((Object[]) rows.get(i))[1]);
			result.put("rank1", ((Object[]) rows.get(i))[0]);
			Object age = ((Object[]) rows.get(i))[2];
			result.put("age1", age);
			result.put("rating", rating);
			for (int j = 2; j <= 7; j++) {
				if (rows.size() >= i + j) {
					Object[] rowArray = (Object[]) rows.get(i + j - 1);
					rating = rowArray[3];
					if (rating.equals(lastRating)) {
						result.put("rank" + j, rowArray[0]);
						result.put("age" + j, rowArray[2]);
					} else {
						i = i -7 + j - 1;
						break;
					}
				} else {
					break;
				}
			}
			results.add(result);
		}
		return jasper.generateReport(getReportFileName(), results, inputParam);

	}

}
