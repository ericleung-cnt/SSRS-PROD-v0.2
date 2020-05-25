package org.mardep.ssrs.dao.seafarer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.Rating;
import org.springframework.stereotype.Repository;

@Repository
public class RatingJpaDao extends AbstractJpaDao<Rating, CommonPK> implements IRatingDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Rating> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("seqNo", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("seafarerId", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("salary", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("rating",		PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("ratingDate", 	PredicateType.EQUAL));
		list.add(new PredicateCriteria("capacityId", 	PredicateType.EQUAL));

		return list;
	}

	@Override
	public Rating findLatestBySeafarerId(String seafarerId){
		Query q = em.createQuery("SELECT r from Rating r where r.seafarerId=:seafarerId order by r.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		try{
			return (Rating) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}

	}

	@Override
	public Map<String, Long> countCompany(Date startDate, Date endDate, String company, String partType){

		return null;
	}

	/**
	 * Report-004
	 *
	 * SELECT COUNT(SR.SEAFARER_ID), SR.RATING
			FROM SEAFARER_RATING SR LEFT JOIN SEAFARER S
			ON SR.SEAFARER_ID = S.SEAFARER_ID
			WHERE S.BIRTH_DATE <= '1999-03-11' GROUP BY SR.RATING
	 *
	 */
	@Override
	public Map<String, Long> countAge(Date asOf, Date startDate, Date endDate, String partType){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT R.ENG_DESC as rank , COUNT(*)  as COUNT FROM ");
		sb.append("SEAFARER_RATING SR INNER JOIN SEAFARER S ON SR.SEAFARER_ID = S.SEAFARER_ID INNER JOIN RANK R ON SR.CAPACITY_ID = R.CAPACITY_ID ");
		sb.append("where exists (select 1 from (select SEAFARER_ID, max(SEQ_NO) SEQ_NO from SEAFARER_RATING where (RATING_DATE <= :asOf OR RATING_DATE IS NULL) group by SEAFARER_ID) X WHERE SR.SEAFARER_ID = X.SEAFARER_ID and SR.SEQ_NO = X.SEQ_NO) ");
		//2019/11/14, add this condition for including RegDate/ Seafarer.STATUS
		sb.append("and   exists (select 1 from (select SEAFARER_ID 					   from SEAFARER_REG    where (REG_DATE    <= :asOf 					  ) group by SEAFARER_ID) Y WHERE SR.SEAFARER_ID = Y.SEAFARER_ID ) ");
		sb.append("and (S.STATUS='A' OR S.STATUS IS NULL)  ");
		
		sb.append("and s.birth_Date between :startDate AND :endDate ");
		if(partType!=null && !partType.isEmpty()){
			sb.append(" and s.part_Type=:partType");
		}
		sb.append(" group by r.eng_Desc ");
		Query query = em.createNativeQuery(sb.toString());
		query.setParameter("asOf", asOf);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		if(partType!=null && !partType.isEmpty()){
			query.setParameter("partType", partType);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		Map<String, Long> resultMap = resultList.stream().collect(Collectors.toMap(o->(String)o[0], o-> new Long((Integer)o[1])));
		return resultMap;
	}


	/**
	 *
	 * Report-005
	 *
	 * Average function ignore null value.
	 *
	 * UPDATED BY Eric Leung on 2019.06.26, assign default value for partType
	 */
	@Override
	public List<Object[]> countAverageWage(Date reportDate, String partType){
		String sql = "select r.department, r.eng_desc, count(*) sum_by_rank, sum(sr.salary) total_rank_salary " +
				"from seafarer s,seafarer_rating sr,rank r,sea_service ss " +
				"where sr.capacity_id = r.capacity_id " +
				"and s.seafarer_id = sr.seafarer_id " +
				"and s.seafarer_id = ss.seafarer_id " +
				"and ss.discharge_date is null " +
				"and ss.employment_date is not null " +
				"and ss.employment_date <= :reportDate " +
				"and ss.seq_no = " +
				"( " +
				"   select " +
				"   max(ss2.seq_no) " +
				"   from sea_service ss2 " +
				"   where ss2.seafarer_id = s.seafarer_id " +
				") " +
				"and sr.seq_no = " +
				"( " +
				"   select " +
				"   max(sr2.seq_no) " +
				"   from seafarer_rating sr2 " +
				"   where sr2.seafarer_id = s.seafarer_id " +
				") " +
				"and " +
				"( " +
				"   ( " +
				"      :partType = '1' " +
				"      AND S.PART_TYPE = 1 " +
				"   ) " +
				"   OR " +
				"   ( " +
				"      :partType = '2' " +
				"      AND S.PART_TYPE = 2 " +
				"   ) " +
				"   OR (:partType = '3' AND S.PART_TYPE IN (1,2)) OR (:partType NOT IN ('1','2','3')) " +
				") " +
				"group by r.department, r.eng_desc ";
		Query query = em.createNativeQuery(sql);
		query.setParameter("reportDate", reportDate);
		if(partType!=null && !partType.isEmpty()){
			query.setParameter("partType", partType);
		} else {
			query.setParameter("partType", "3");
		}

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		return resultList;
	}


	/**
	 *
	 * Report-006
	 * UPDATED BY Eric Leung 2019.06.26, changed query
	 *
	 */
	@Override
	public Map<String, Long> countAgeWaitingEmployment(Date startDate, Date endDate, String partType){
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT r.ENG_DESC AS RANK, COUNT(*) AS COUNT "
				+ "from seafarer s, " +
				"       seafarer_rating sr, " +
				"       rank r, " +
				"       sea_service ss " +
				"  where sr.capacity_id = r.capacity_id and s.seafarer_id = sr.seafarer_id and sr.seq_no = (select max(sr2.seq_no) from seafarer_rating sr2 where sr2.seafarer_id = s.seafarer_id) " +
				"    and ss.seafarer_id = s.seafarer_id and ss.discharge_date is not null " +
				"    and ss.seq_no = (select max(ss2.seq_no) from sea_service ss2 where ss2.seafarer_id = s.seafarer_id) ");
		sb.append(" AND s.BIRTH_DATE between :startDate AND :endDate ");
		if(partType!=null && !partType.isEmpty()){
			sb.append(" AND s.PART_TYPE=:partType");
		}
		sb.append(" GROUP BY r.ENG_DESC ");
		Query query = em.createNativeQuery(sb.toString());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		if(partType!=null && !partType.isEmpty()){
			query.setParameter("partType", partType);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		Map<String, Long> resultMap = resultList.stream().collect(Collectors.toMap(o->(String)o[0], o->new Long((Integer)o[1])));
		return resultMap;
	}

	/**
	 *
	 * Report-007, count rank, group by nationality
	 * UPDATED BY Eric Leung 2019.06.25, changed query
	 *
	 */
	@Override
	public List<Object[]> countRank(Date reportDate, String rankRating){
		StringBuffer sb = new StringBuffer();
//		sb.append(" SELECT N.ENG_DESC AS NATIONALITY, COUNT(SR.SEAFARER_ID) AS NO FROM SEAFARER_RATING SR   ");
//		sb.append(" LEFT JOIN SEAFARER S ON SR.SEAFARER_ID = S.SEAFARER_ID ");
//		sb.append(" LEFT JOIN RANK R ON SR.CAPACITY_ID = R.CAPACITY_ID ");
//		sb.append(" LEFT JOIN NATIONALITY N ON S.NATIONALITY_ID = N.NATIONALITY_ID ");
//		sb.append(" WHERE SR.RATING_DATE<=:reportDate ");
//		sb.append(" AND R.RANK_RATING =:rankRating ");
//		sb.append(" GROUP BY N.ENG_DESC ORDER BY N.ENG_DESC ");

		sb.append(" SELECT N.ENG_DESC AS NATIONALITY, COUNT(C.SEAFARER_NAME) AS NO FROM CREW C   ");
		sb.append(" LEFT JOIN RANK R ON C.CAPACITY_ID = R.CAPACITY_ID ");
		sb.append(" LEFT JOIN NATIONALITY N ON C.NATIONALITY_ID = N.NATIONALITY_ID ");
		sb.append(" WHERE C.ENGAGE_DATE<=:reportDate ");
		sb.append(" AND R.RANK_RATING =:rankRating ");
		sb.append(" GROUP BY N.ENG_DESC ORDER BY N.ENG_DESC ");

//		select n.ENG_DESC, count(c.seafarer_name) from crew c
//		left join [rank] r on c.CAPACITY_ID = r.CAPACITY_ID
//		left join NATIONALITY n on c.NATIONALITY_ID = n.NATIONALITY_ID
//		where c.cover_yymm like '180%'
//		group by n.ENG_DESC

		Query query = em.createNativeQuery(sb.toString());
		query.setParameter("reportDate", reportDate);
		query.setParameter("rankRating", rankRating);

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		return resultList;

	}
	/**
	 *
	 * Report-008
	 * UPDATED BY Eric Leung 2019.06.25, changed query
	 *
	 */
	@Override
	public List<Object[]> countNationalityRank(Date reportDate, Long nationalityId, String rankRating){
		StringBuffer sb = new StringBuffer();
//		sb.append(" SELECT R.ENG_DESC AS RANK, COUNT(SR.SEAFARER_ID) AS NO FROM SEAFARER_RATING SR  ");
//		sb.append(" LEFT JOIN SEAFARER S ON SR.SEAFARER_ID = S.SEAFARER_ID ");
//		sb.append(" LEFT JOIN RANK R ON SR.CAPACITY_ID = R.CAPACITY_ID ");
//		sb.append(" WHERE S.NATIONALITY_ID =:nationalityId");
//		sb.append(" AND SR.RATING_DATE<=:reportDate");
//		sb.append(" AND R.RANK_RATING =:rankRating ");
//		sb.append(" GROUP BY R.ENG_DESC ");

		sb.append(" SELECT R.ENG_DESC AS NATIONALITY, COUNT(C.SEAFARER_NAME) AS NO FROM CREW C   ");
		sb.append(" LEFT JOIN RANK R ON C.CAPACITY_ID = R.CAPACITY_ID ");
		sb.append(" LEFT JOIN NATIONALITY N ON C.NATIONALITY_ID = N.NATIONALITY_ID ");
		sb.append(" WHERE C.NATIONALITY_ID = :nationalityId");
		sb.append("	AND C.ENGAGE_DATE<=:reportDate ");
		sb.append(" AND R.RANK_RATING =:rankRating ");
		sb.append(" GROUP BY R.ENG_DESC ");

		Query query = em.createNativeQuery(sb.toString());
		query.setParameter("reportDate", reportDate);
		query.setParameter("nationalityId", nationalityId);
		query.setParameter("rankRating", rankRating);

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		return resultList;

	}

	/**
	 *
	 * Report-009
	 *
	 */
	@Override
	public List<Object[]> sumSalaryByRank(Date reportDate, String rankRating){
		Query query = em.createNativeQuery("select r.ENG_DESC, case when count(salary) = 0 then 0 else sum(salary)/count(salary) end avgsalary " +
				"from CREW c " +
				"inner join RANK r on c.CAPACITY_ID = r.CAPACITY_ID " +
				"where :reportDate between engage_date and isnull(discharge_date, convert(datetime, '3019-12-31',102))   and R.RANK_RATING =:rankRating " +
				"group by r.CAPACITY_ID , r.ENG_DESC ");
		query.setParameter("reportDate", reportDate);
		query.setParameter("rankRating", rankRating);

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		return resultList;

	}

	/**
	 *
	 * Report-010 : Average Monthly Wages by Rank/Rating by Nationality
	 *
	 */
	@Override
	public List<Object[]> avgSalaryByRankByNationality(Date reportDateFrom, Date reportDateTo, Long nationalityId){
		Query query = em.createNativeQuery("select r.ENG_DESC, case when count(salary) = 0 then 0 else sum(salary)/count(salary) end avgsalary " +
				"from CREW c " +
				"inner join RANK r on c.CAPACITY_ID = r.CAPACITY_ID " +
				//"where :reportDate between engage_date and discharge_date  and R.RANK_RATING =:rankRating " +
				"where engage_date between :reportDateForm and :reportDateTo and nationality_id = :nationalityId " +
				"group by r.CAPACITY_ID , r.ENG_DESC ");
		query.setParameter("reportDateFrom", reportDateFrom);
		query.setParameter("reportDateTo", reportDateTo);
		query.setParameter("nationalityId", nationalityId);

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		return resultList;

	}

	@Override
	public Map<String, Object[]> getEmploymentSituation(Date reportDate, String partType, String reportType) {
		String criteria = "";
		if (partType != null) {
			switch (partType) {
			case "1":
				criteria += "      and PART_TYPE  = '1' ";
				break;
			case "2":
				criteria += "      and PART_TYPE  = '2' ";
				break;
			}
		}
		switch (reportType) {
		case "1": // Now Serving on Board
			criteria += "      and DISCHARGE_DATE is null ";
			break;
		case "2": // Listed with Permitted Company
			criteria += "      and SE.CANCEL_DATE is null and SE.LISTING_DATE is not null and SE.LISTING_DATE <= :reportDate ";
			break;
		case "3": // Onboard over 12 months
			criteria += "      and SS.DISCHARGE_DATE is null and SS.EMPLOYMENT_DATE < DATEADD(year, -1, :reportDate) ";
			break;
		case "4": // Discharge less than 12 months
			criteria += "      and SS.DISCHARGE_DATE between DATEADD(year, -1, :reportDate) and DATEADD(year, 0, :reportDate) ";
			break;
		case "5": // Discharge 12 to 24 months
			criteria += "      and SS.DISCHARGE_DATE between DATEADD(year, -2, :reportDate) and DATEADD(year, -1, :reportDate) ";
			break;
		case "6": // Discharge over 24 months
			criteria += "      and SS.DISCHARGE_DATE < DATEADD(year, -2, :reportDate)  ";
			break;
		}


		String sql = "select " +
				"RANKING, " +
				"sum (case when age_group = 1 then 1 else 0 end) group_1, " +
				"sum (case when age_group = 2 then 1 else 0 end) group_2, " +
				"sum (case when age_group = 3 then 1 else 0 end) group_3, " +
				"sum (case when age_group = 4 then 1 else 0 end) group_4, " +
				"sum (case when age_group = 5 then 1 else 0 end) group_5, " +
				"sum (case when age_group = 6 then 1 else 0 end) group_6, " +
				"count (*) group_7 " +
				"from " +
				"( " +
				"   select " +
				"   case " +
				"when age <= 20 then 1 " +
				"when age <= 30 then 2 " +
				"when age <= 40 then 3 " +
				"when age <= 50 then 4 " +
				"when age <= 60 then 5 " +
				"else 6 end age_group, " +
				"   DEPARTMENT, " +
				"   RANKING " +
				"   from " +
				"   ( " +
				"      select " +
				"      year(:reportDate) - year(birth_date) + (case when RIGHT(Convert(varchar, :reportDate, 12), 4) >= RIGHT(Convert(varchar, birth_date, 12), 4) then 1 else 0 end) age, " +
				"      SFR.BIRTH_DATE, " +
				"      R.DEPARTMENT, " +
				"      R.ENG_DESC RANKING " +
				"      from SEAFARER SFR ";
		sql += !"2".equals(reportType) ? "" :
				"      inner join " +
				"      ( " +
				"         select " +
				"         * " +
				"         from SEAFARER_EMPLOYMENT SE " +
				"         where exists " +
				"         ( " +
				"            select " +
				"            1 " +
				"            from " +
				"            ( " +
				"               select " +
				"               SEAFARER_ID, " +
				"               MAX(SEQ_NO) SEQ_NO " +
				"               from SEAFARER_EMPLOYMENT " +
				"               group by SEAFARER_ID " +
				"            ) " +
				"            LR " +
				"            where SE.SEAFARER_ID = LR.SEAFARER_ID " +
				"            and SE.SEQ_NO = LR.SEQ_NO " +
				"         ) " +
				"      ) " +
				"      SE on SFR.SEAFARER_ID = SE.SEAFARER_ID ";
		sql +=
				"      inner join " +
				"      ( " +
				"         select " +
				"         * " +
				"         from SEAFARER_RATING SR " +
				"         where exists " +
				"         ( " +
				"            select " +
				"            1 " +
				"            from " +
				"            ( " +
				"               select " +
				"               SEAFARER_ID, " +
				"               MAX(SEQ_NO) SEQ_NO " +
				"               from SEAFARER_RATING " +
				"               group by SEAFARER_ID " +
				"            ) " +
				"            LR " +
				"            where SR.SEAFARER_ID = LR.SEAFARER_ID " +
				"            and SR.SEQ_NO = LR.SEQ_NO " +
				"         ) " +
				"      ) " +
				"      SR on SFR.SEAFARER_ID = SR.SEAFARER_ID " +
				"      inner join " +
				"      ( " +
				"         select " +
				"         * " +
				"         from SEA_SERVICE SS " +
				"         where exists " +
				"         ( " +
				"            select " +
				"            1 " +
				"            from " +
				"            ( " +
				"               select " +
				"               SEAFARER_ID, " +
				"               MAX(SEQ_NO) SEQ_NO " +
				"               from SEA_SERVICE " +
				"               group by SEAFARER_ID " +
				"            ) " +
				"            LR " +
				"            where SS.SEAFARER_ID = LR.SEAFARER_ID " +
				"            and SS.SEQ_NO = LR.SEQ_NO " +
				"         ) " +
				"      ) " +
				"      SS on SFR.SEAFARER_ID = SS.SEAFARER_ID " +
				"      inner join RANK R on SR.CAPACITY_ID = R.CAPACITY_ID " +
				"      where 1 = 1 " + criteria +
				"   ) X ) X group by DEPARTMENT, RANKING ";

		Query query = em.createNativeQuery(sql);
		query.setParameter("reportDate", reportDate);
		Map<String, Object[]> map = new HashMap<>();
		List resultList = query.getResultList();
		for (Object row : resultList) {
			Object[] array = (Object[]) row;
			Object[] data = new Object[6];
			System.arraycopy(array, 1, data, 0, 6);
			map.put((String) array[0], data);
		}
		return map;
	}

	@Override
	public List<Object[]> sumSalaryByShipType(Date reportDate, String shipTypeCode) {
		Query query = em.createNativeQuery("select r.ENG_DESC, case when count(salary) = 0 then 0 else sum(salary)/count(salary) end avgsalary,"
				+ " case when count(salary) = 0 then 0 else count(salary) end countsalary " +
				"from CREW c "
				+ "inner join REG_MASTERS rm on c.VESSEL_ID= rm.APPL_NO  " +
				"inner join RANK r on c.CAPACITY_ID = r.CAPACITY_ID " +
				"where :reportDate between engage_date and isnull(discharge_date, convert(datetime, '3019-12-31',102))   "
				+ "and rm.SS_ST_SHIP_TYPE_CODE =:shipTypeCode " +
				"group by r.CAPACITY_ID , r.ENG_DESC ");
		query.setParameter("reportDate", reportDate);
		query.setParameter("shipTypeCode", shipTypeCode);

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		return resultList;
	}
}
