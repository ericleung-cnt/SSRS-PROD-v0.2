package org.mardep.ssrs.dao.seafarer;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.Rating;

public interface IRatingDao extends IBaseDao<Rating, CommonPK> {

	Rating findLatestBySeafarerId(String seafarerId);

	/**
	 * For MMO Report: 003: No. of Seafarers (Part 1) Listed with Permitted Company
	 *
	 * @param startDate
	 * @param endDate
	 * @param partType
	 * @return
	 */
	Map<String, Long> countCompany(Date startDate, Date endDate, String company, String partType);

	/**
	 * for MMO Report 004: Summary of Registration of HK Registered Seafarer
	 *
	 * key:rating
	 * value:count
	 *
	 * @param startDate
	 * @param endDate
	 * @param date 
	 * @param partType null means all
	 * @return
	 */
	Map<String, Long> countAge(Date asOf, Date startDate, Date endDate, String partType);

	/**
	 * for MMO Report-005: Summary of Average Wages of HK Registered Seafarer
	 *
	 * @param reportDate
	 * @param partType
	 * @return
	 */
//	Map<String, Long> countAverageWage(Date reportDate, String partType);
	List<Object[]> countAverageWage(Date reportDate, String partType);

	/**
	 * for MMO Report 006: Summary of Seafarer Waiting for Employment
	 *
	 * @param startDate
	 * @param endDate
	 * @param partType
	 * @return
	 */
	Map<String, Long> countAgeWaitingEmployment(Date startDate, Date endDate, String partType);

	/**
	 * for MMO Report 007: Distribution of Crew Nationality of Hong Kong Ships, count rank, group by nationality
	 *
	 * @param reportDate
	 * @param nationalityId
	 * @param rankRating
	 * @return
	 */
	List<Object[]> countRank(Date reportDate, String rankRating);

	/**
	 * for MMO Report 008: Distribution of Crew by Rank / Rating by Nationality
	 *
	 * @param reportDate
	 * @param nationalityId
	 * @param rankRating
	 * @return
	 */
	List<Object[]> countNationalityRank(Date reportDate, Long nationalityId, String rankRating);

	/**
	 *
	 * for MMO Report 009: Average Monthly Wages by Rank
	 *
	 * @param reportDate
	 * @param rankRating
	 * @return
	 */
	List<Object[]> sumSalaryByRank(Date reportDate, String rankRating);

	/**
	 * MMO-002
	 * @param reportDate
	 * @param partType
	 * @param reportType
	 * @return
	 */
	Map<String, Object[]> getEmploymentSituation(Date reportDate, String partType, String reportType);

	/**
	 *
	 * Report-010 : Average Monthly Wages by Rank/Rating by Nationality
	 *
	 */
	List<Object[]> avgSalaryByRankByNationality(Date reportDateFrom, Date reportDateTo, Long nationalityId);

	List<Object[]> sumSalaryByShipType(Date reportDate, String shipTypeCode);

}
