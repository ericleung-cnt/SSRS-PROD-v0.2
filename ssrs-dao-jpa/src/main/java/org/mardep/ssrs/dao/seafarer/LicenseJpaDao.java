package org.mardep.ssrs.dao.seafarer;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.License;
import org.springframework.stereotype.Repository;

@Repository
public class LicenseJpaDao extends AbstractJpaDao<License, CommonPK> implements ILicenseDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<License> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("seqNo", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("seafarerId", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("courseDesc", 	PredicateType.LIKE_IGNORE_CASE));

		return list;
	}
	
	@Override
	public License findLatestBySeafarerId(String seafarerId){
		Query q = em.createQuery("SELECT l from License l where l.seafarerId=:seafarerId order by l.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		try{
			return (License) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
	}
	
	@Override
	public License findLatestCustom(String seafarerId){
		Query q = em.createQuery("SELECT l from License l where l.seafarerId=:seafarerId and l.seqNo>100 order by l.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		try{
			return (License) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
	}
	
	@Override
	public License findBySeafarerIdSeqNo(String seafarerId, Integer seqNo){
		Query q = em.createQuery("SELECT l from License l where l.seafarerId=:seafarerId order by l.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		try{
			return (License) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
		
	}
}
