package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.CallSign;
import org.mardep.ssrs.domain.codetable.CallSignPK;
import org.springframework.stereotype.Repository;

@Repository
public class CallSignJpaDao extends AbstractJpaDao<CallSign, CallSignPK> implements ICallSignDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<CallSign> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("csCallSign", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("csAvailFlat", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}
}
