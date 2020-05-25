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
import org.mardep.ssrs.domain.seafarer.NextOfKin;
import org.springframework.stereotype.Repository;

@Repository
public class NextOfKinJpaDao extends AbstractJpaDao<NextOfKin, CommonPK> implements INextOfKinDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<NextOfKin> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("seqNo", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("seafarerId", 	PredicateType.LIKE_IGNORE_CASE));
		
		list.add(new PredicateCriteria("kinName", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("kinChiName", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("kinHkid", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("relation", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("marriageCertNo", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("addr1", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("addr2", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("addr3", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("telephone", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("status", 		PredicateType.EQUAL));

		return list;
	}
	
	@Override
	public NextOfKin findLatestBySeafarerId(String seafarerId){
		Query q = em.createQuery("SELECT nk from NextOfKin nk where nk.seafarerId=:seafarerId order by nk.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		try{
			return (NextOfKin) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}

	}
}
