package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.MortgagePK;
import org.springframework.stereotype.Repository;

@Repository
public class MortgageJpaDao extends AbstractJpaDao<Mortgage, MortgagePK> implements IMortgageDao {
	private List<PredicateCriteria> criteriaList = new ArrayList<PredicateCriteria>();

	public MortgageJpaDao() {
		criteriaList.add(new PredicateCriteria("applNo", PredicateType.EQUAL));
		criteriaList.add(new PredicateCriteria("priorityCode", PredicateType.EQUAL));
	}

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb, Root<Mortgage> listRoot) {
		return criteriaList;
	}

	@Override
	public Map<String, String> findMortgageRegDateNatures(String applNo) {
		Query query = em.createNativeQuery("select MOR_PRIORITY_CODE, DATE_CHANGE, HOUR_CHANGE, TXN_NATURE_DETAILS "
				+ "from MORTGAGES_HIST H "
				+ "INNER JOIN TRANSACTIONS T "
				+ "on H.TX_ID = T.AT_SER_NUM "
				+ "and T.TC_TXN_CODE = '33' "
				+ "WHERE MOR_RM_APPL_NO = :applNo");
		query.setParameter("applNo", applNo);
		List list = query.getResultList();
		Map<String, String> result = new HashMap<>();
		for (Object row:list) {
			Object[] array = (Object[]) row;
			String value = String.valueOf(array[1]).substring(0,10) + array[2] + "\n" + array[3];
			result.put((String) array[0], value);
		}

		return result;
	}

	@Override
	public List<Mortgage> findByApplId(String applNo) {
		Query query = em.createQuery("select mortgage from Mortgage mortgage where mortgage.applNo = :applNo");
		query.setParameter("applNo", applNo);
		return query.getResultList();
	}

	@Override
	public char nextPriority(String applNo) {
		Query query = em.createNativeQuery(""
				+ "select max(MOR_PRIORITY_CODE) from (select MOR_PRIORITY_CODE from  mortgages where MOR_RM_APPL_NO = "
				+ ":applNo"
				+ " union select MOR_PRIORITY_CODE from  mortgages_HIST where MOR_RM_APPL_NO = "
				+ ":applNo"
				+ ") cx"
				+ "");
		query.setParameter("applNo", applNo);
		List list = query.getResultList();
		if (list.isEmpty()) {
			return 'A';
		} else {
			String code = (String) list.get(0);
			if (code == null) {
				return 'A';
			}
			return (char) (code.charAt(0) + 1);
		}
	}

}
