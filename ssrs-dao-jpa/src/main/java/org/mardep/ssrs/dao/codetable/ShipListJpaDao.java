package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.ShipList;
import org.springframework.stereotype.Repository;

@Repository
public class ShipListJpaDao extends AbstractJpaDao<ShipList, Integer> implements IShipListDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<ShipList> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("partType", PredicateType.EQUAL));
		list.add(new PredicateCriteria("vesselName", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("companyName", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("flag", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("noOfReg", PredicateType.EQUAL));
		list.add(new PredicateCriteria("noOfExempt", PredicateType.EQUAL));
		list.add(new PredicateCriteria("noOfForeign", PredicateType.EQUAL));
		list.add(new PredicateCriteria("remark", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}
}
