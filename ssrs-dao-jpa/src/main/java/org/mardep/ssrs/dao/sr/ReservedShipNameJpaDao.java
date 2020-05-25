package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.ReservedShipName;
import org.mardep.ssrs.domain.sr.ReservedShipNamePK;
import org.springframework.stereotype.Repository;

@Repository
public class ReservedShipNameJpaDao extends AbstractJpaDao<ReservedShipName, ReservedShipNamePK> implements IReservedShipNameDao {
	private List<PredicateCriteria> criteria = new ArrayList<>();
	public ReservedShipNameJpaDao() {
		criteria.add(new PredicateCriteria("name", PredicateType.EQUAL));
		criteria.add(new PredicateCriteria("chiName", PredicateType.EQUAL));
		criteria.add(new PredicateCriteria("expiryDate", PredicateType.GREATER_OR_EQUAL));
	}
	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb,
			Root<ReservedShipName> listRoot) {
		return criteria;
	}
}
