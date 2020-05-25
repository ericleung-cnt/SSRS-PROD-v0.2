package org.mardep.ssrs.dao.seafarer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.Employment;
import org.springframework.stereotype.Repository;

@Repository
public class EmploymentJpaDao extends AbstractJpaDao<Employment, CommonPK> implements IEmploymentDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Employment> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("seqNo", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("seafarerId", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("companyName", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("listingDate", 	PredicateType.EQUAL));
		list.add(new PredicateCriteria("cancelDate", 	PredicateType.EQUAL));

		return list;
	}
	
	@Override
	public Employment findLatestBySeafarerId(String seafarerId){
		Query q = em.createQuery("SELECT e from Employment e where e.seafarerId=:seafarerId order by e.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		try{
			return (Employment) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Employment> findBySeafarerId(String seafarerId){
		Query q = em.createQuery("SELECT e from Employment e where e.seafarerId=:seafarerId order by e.listingDate desc, e.seqNo desc ");
		q.setParameter("seafarerId", seafarerId);
		try{
			return q.getResultList();
		}catch (Exception nre){
			return Collections.emptyList();
		}
	}
}
