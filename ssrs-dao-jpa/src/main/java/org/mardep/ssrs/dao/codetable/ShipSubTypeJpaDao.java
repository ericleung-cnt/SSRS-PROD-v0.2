package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.ShipSubType;
import org.mardep.ssrs.domain.codetable.ShipSubTypePK;
import org.springframework.stereotype.Repository;

@Repository
public class ShipSubTypeJpaDao extends AbstractJpaDao<ShipSubType, ShipSubTypePK> implements IShipSubTypeDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<ShipSubType> listRoot) {
		listRoot.fetch("shipType", JoinType.LEFT);
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("shipTypeCode", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("shipSubTypeCode", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("ssDesc", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}
}
