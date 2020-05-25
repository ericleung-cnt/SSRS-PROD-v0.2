package org.mardep.ssrs.dao.codetable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.Rank;
import org.springframework.stereotype.Repository;

@Repository
public class RankJpaDao extends AbstractJpaDao<Rank, Long> implements IRankDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Rank> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("engDesc", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("chiDesc", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("department", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("rankRating", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}

	@Override
	public List getAverageWagesByNationality(Date reportDateFrom, Date reportDateTo, Long nationalityId) {
		List nationalityResult = em.createNativeQuery("select ENG_DESC from NATIONALITY where NATIONALITY_ID = :nationalityId").setParameter("nationalityId", nationalityId).getResultList();
		String nationality = (nationalityResult.size() == 1) ? (String) nationalityResult.get(0) : "";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("reportDateFrom", reportDateFrom);
		parameters.put("reportDateTo", reportDateTo);
		List reportList = new ArrayList<>();
		reportList.add(parameters);
		if (!nationality.isEmpty()) {
			parameters.put("nationality", nationality);
			String from = "from " +
								"( " +
								"   select " +
								"   SALARY, " +
								"   R.ENG_DESC RANK, " +
								"   COUNTRY_ENG_DESC COUNTRY, " +
								"   ( " +
								"      select " +
								"      top(1) OT_OPER_TYPE_CODE " +
								"      from REG_MASTERS " +
								"      where IMO_NO = cl.IMO_NO and OT_OPER_TYPE_CODE is not null " +
								"      order by APPL_NO desc " +
								"   ) " +
								"   OT_OPER_TYPE_CODE " +
								"   from CREW c " +
								"   inner join NATIONALITY n on c.NATIONALITY_ID = n.NATIONALITY_ID " +
								"   inner join RANK r on C.CAPACITY_ID = R.CAPACITY_ID " +
								"   inner join CREW_LIST_COVER cl on c.VESSEL_ID = cl.VESSEL_ID " +
								"   where ((ENGAGE_DATE <= :reportDateFrom " +
								"   and isnull(discharge_date, convert(datetime, '3019-12-31',102))  >= :reportDateTo) " +
								" or (DISCHARGE_DATE BETWEEN :reportDateFrom and :reportDateTo)  " +
								" or (ENGAGE_DATE BETWEEN :reportDateFrom and :reportDateTo) )  " +
								"   and c.NATIONALITY_ID = :nationalityId " +
								") " +
								"a where RANK is not null and OT_OPER_TYPE_CODE is not null ";
			Query query = em.createNativeQuery("select " +
					"RANK,OT_OPER_TYPE_CODE,avg(SALARY) wages " +
					from +
					"group by RANK,OT_OPER_TYPE_CODE ");
			query.setParameter("reportDateFrom", reportDateFrom);
			query.setParameter("reportDateTo", reportDateTo);
			query.setParameter("nationalityId", nationalityId);
			List resultList = query.getResultList();
			HashMap<String, Integer> heads = new HashMap<>();
			HashMap<String, Integer> ranks = new HashMap<>();
			for (int i = 0; i < resultList.size(); i++) {
				Object[] row = (Object[]) resultList.get(i);
				if (!heads.containsKey(row[1])) {
					heads.put((String) row[1], heads.size());
					parameters.put("head" + heads.size(), row[1]);
				}
				if (!ranks.containsKey(row[0])) {
					ranks.put((String) row[0], ranks.size());
					reportList.add(new HashMap<>(parameters));
				}
			}
			int averageIndex = -1;
			if (!heads.isEmpty() && heads.size() < 12) {
				averageIndex = heads.size() + 1;
				parameters.put("head" + averageIndex, "Average");
			}
			DecimalFormat format = new DecimalFormat("$#,###");
			for (int i = 0; i < resultList.size(); i++) {
				Object[] row = (Object[]) resultList.get(i);
				String rank = (String) row[0];
				String type = (String) row[1];
				Map fields = (Map) reportList.get(ranks.get(rank) + 1);
				fields.put("field0", rank);
				fields.put("field" + (heads.get(type) + 1), format.format(row[2]));
			}
			if (!resultList.isEmpty() && averageIndex != -1) {
				Query summary = em.createNativeQuery("select " +
						"RANK,avg(SALARY) wages " +
						from +
						"group by RANK ");
				summary.setParameter("reportDateFrom", reportDateFrom);
				summary.setParameter("reportDateTo", reportDateTo);
				summary.setParameter("nationalityId", nationalityId);
				List averages = summary.getResultList();
				for (int i = 0 ; i < averages.size(); i++) {
					Object[] row = (Object[]) averages.get(i);
					String rank = (String) row[0];
					Map fields = (Map) reportList.get(ranks.get(rank) + 1);
					fields.put("field" + averageIndex, format.format(row[1]));
				}
			}
		}
		return reportList;
	}

	@Override
	public List<Object> getAverageAgeByNationality(Date reportDate, Long nationality) {
		String sql = "select " +
				"r.ENG_DESC RANK, n.ENG_DESC, " +
				"isnull(avg " +
				"((YEAR(:reportDate) - YEAR(BIRTH_DATE))+ case when(MONTH(BIRTH_DATE) " +
				"      *100+DAY(BIRTH_DATE) < MONTH(:reportDate)*100+DAY(:reportDate)) then 0 else -1 end), 0) age, " +
				"r.RANK_RATING " +
				"from RANK r cross join NATIONALITY n " +
				"LEFT OUTER JOIN CREW c on c.CAPACITY_ID = r.CAPACITY_ID and n.NATIONALITY_ID = c.NATIONALITY_ID and  ENGAGE_DATE <= :reportDate and (DISCHARGE_DATE is null or DISCHARGE_DATE >= :reportDate) " +
				"where 1 = 1 " +
				"and n.NATIONALITY_ID = :nationality " +
				"group by R.RANK_RATING, R.CAPACITY_ID,r.ENG_DESC, n.ENG_DESC "
				+ "order by R.RANK_RATING, R.CAPACITY_ID, r.ENG_DESC, n.ENG_DESC ";
		Query query = em.createNativeQuery(sql);
		query.setParameter("reportDate", reportDate);
		query.setParameter("nationality", nationality);

		return query.getResultList();
	}
}
