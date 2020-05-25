package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.springframework.stereotype.Repository;

@Repository
public class FeeCodeJpaDao extends AbstractJpaDao<FeeCode, String> implements IFeeCodeDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<FeeCode> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("formCode", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("engDesc", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("chiDesc", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("feePrice", PredicateType.EQUAL));
		list.add(new PredicateCriteria("active", PredicateType.EQUAL));

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FeeCode> findForFormCode(List<String> formCodes) {
		Query q  = em.createQuery("select f from FeeCode f where f.formCode in:formCodes order by f.id asc ");
		q.setParameter("formCodes", formCodes);
		return (List<FeeCode>) q.getResultList();
	}
}
