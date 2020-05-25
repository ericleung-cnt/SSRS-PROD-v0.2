package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.codetable.Nationality;
import org.springframework.stereotype.Repository;

@Repository
public class NationalityJpaDao extends AbstractJpaDao<Nationality, Long> implements INationalityDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Nationality> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("engDesc", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("chiDesc", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}

	@Override
	protected void resolveOrderBy(SortByCriteria sortByCriteria, CriteriaBuilder cb, CriteriaQuery<?> queryList,
			Root<Nationality> listRoot) {
		super.resolveOrderBy(sortByCriteria, cb, queryList, listRoot);
		queryList.orderBy(cb.asc(listRoot.get("engDesc")));
	}
}
