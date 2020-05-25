package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.Vessel;
import org.springframework.stereotype.Repository;

@Repository
public class VesselJpaDao extends AbstractJpaDao<Vessel, String> implements IVesselDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Vessel> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("name", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("chiName", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("vesselType", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("status", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}
}
