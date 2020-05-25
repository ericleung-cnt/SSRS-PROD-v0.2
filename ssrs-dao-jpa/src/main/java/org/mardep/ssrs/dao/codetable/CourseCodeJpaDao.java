package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.CourseCode;
import org.springframework.stereotype.Repository;

@Repository
public class CourseCodeJpaDao extends AbstractJpaDao<CourseCode, Integer> implements ICourseCodeDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<CourseCode> listRoot) {
//		listRoot.fetch("feeCode", JoinType.LEFT);
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
//		list.add(new PredicateCriteria("fcFeeCode", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("institue", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("engDesc", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("chiDesc", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("status", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}
}
