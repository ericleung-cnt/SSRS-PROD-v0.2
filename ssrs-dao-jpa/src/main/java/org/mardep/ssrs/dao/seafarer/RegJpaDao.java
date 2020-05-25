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
import org.mardep.ssrs.domain.seafarer.Reg;
import org.springframework.stereotype.Repository;

@Repository
public class RegJpaDao extends AbstractJpaDao<Reg, CommonPK> implements IRegDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Reg> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("seqNo", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("seafarerId", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("regDate", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("regExpiry", 	PredicateType.EQUAL));
		list.add(new PredicateCriteria("regCancel", 	PredicateType.EQUAL));

		return list;
	}
	
	@Override
	public Reg findLatestBySeafarerId(String seafarerId) {
		Query q = em.createQuery("SELECT r from Reg r where r.seafarerId=:seafarerId order by r.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		try{
			return (Reg) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}

	}
}
