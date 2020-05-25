package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.Clinic;
import org.springframework.stereotype.Repository;

@Repository
public class ClinicJpaDao extends AbstractJpaDao<Clinic, Long> implements IClinicDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Clinic> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("name", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("chiName", PredicateType.LIKE_IGNORE_CASE));

		list.add(new PredicateCriteria("adds1", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("adds2", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("adds3", PredicateType.LIKE_IGNORE_CASE));
		
		list.add(new PredicateCriteria("chiAdds1", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("chiAdds2", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("chiAdds3", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("tel", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("email", PredicateType.LIKE_IGNORE_CASE));

		list.add(new PredicateCriteria("noq", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("year", PredicateType.EQUAL));
		list.add(new PredicateCriteria("regNo", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("recognizedIndicator", PredicateType.EQUAL));

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Clinic> findEnabled() {
		try{
			Query q = em.createQuery("SELECT c from Clinic c where c.deleteDate is null or c.deleteDate>:current");
			q.setParameter("current", new Date(), TemporalType.DATE);
			return q.getResultList();
		}catch (Exception nre){
			return Collections.emptyList();
		}
	}
}
