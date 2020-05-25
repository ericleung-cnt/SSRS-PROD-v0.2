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
import org.mardep.ssrs.domain.seafarer.PreviousSerb;
import org.springframework.stereotype.Repository;

@Repository
public class PreviousSerbJpaDao extends AbstractJpaDao<PreviousSerb, CommonPK> implements IPreviousSerbDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<PreviousSerb> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("seqNo", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("seafarerId", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("serbNo", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("serbDate", 	PredicateType.EQUAL));

		return list;
	}

	@Override
	public PreviousSerb findLatestBySeafarerId(String seafarerId) {
		Query q = em.createQuery("SELECT ps from PreviousSerb ps where ps.seafarerId=:seafarerId order by ps.serbStartDate desc, ps.seqNo desc ");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		
		try{
			return (PreviousSerb) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PreviousSerb> findBySeafarerId(String seafarerId) {
		Query q = em.createQuery("SELECT ps from PreviousSerb ps where ps.seafarerId=:seafarerId order by ps.serbStartDate desc, ps.seqNo desc ");
		q.setParameter("seafarerId", seafarerId);
		try{
			return q.getResultList();
		}catch (Exception nre){
			return Collections.emptyList();
		}
	}

	@Override
	public PreviousSerb findBySerbNo(String serbNo) {
		try{
			Query q = em.createQuery("SELECT ps from PreviousSerb ps where ps.serbNo=:serbNo");
			q.setMaxResults(1);
			q.setParameter("serbNo", serbNo);
			return (PreviousSerb) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
	}
}
