package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.Injuction;
import org.mardep.ssrs.domain.sr.InjuctionPK;
import org.springframework.stereotype.Repository;

@Repository
public class InjuctionJpaDao extends AbstractJpaDao<Injuction, InjuctionPK> implements IInjuctionDao {
	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb, Root<Injuction> listRoot) {
		ArrayList<PredicateCriteria> criteria = new ArrayList<PredicateCriteria>();
		criteria.add(new PredicateCriteria("applNo", PredicateType.EQUAL));
		return criteria;
	}
}
