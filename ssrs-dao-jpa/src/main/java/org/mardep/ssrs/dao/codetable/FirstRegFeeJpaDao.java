package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.FirstRegFee;
import org.mardep.ssrs.domain.codetable.FirstRegFeePK;
import org.springframework.stereotype.Repository;

@Repository
public class FirstRegFeeJpaDao extends AbstractJpaDao<FirstRegFee, FirstRegFeePK> implements IFirstRegFeeDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<FirstRegFee> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("rfsTonLo", PredicateType.EQUAL));
		list.add(new PredicateCriteria("rfsTonHi", PredicateType.EQUAL));
		list.add(new PredicateCriteria("rfsFee", PredicateType.EQUAL));
		return list;
	}
}
