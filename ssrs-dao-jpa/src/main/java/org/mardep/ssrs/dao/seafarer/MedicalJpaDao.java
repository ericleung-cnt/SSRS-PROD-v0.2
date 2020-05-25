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
import org.mardep.ssrs.domain.seafarer.Medical;
import org.springframework.stereotype.Repository;

@Repository
public class MedicalJpaDao extends AbstractJpaDao<Medical, CommonPK> implements IMedicalDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Medical> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("seqNo", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("seafarerId", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("expiryDate", 	PredicateType.EQUAL));
		list.add(new PredicateCriteria("medicalRemark", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("doctorRemark", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("xrayRemark", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("examPlace", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("examDate", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("clinicNo", 		PredicateType.EQUAL));

		return list;
	}
	
	@Override
	public Medical findLatestBySeafarerId(String seafarerId){
		Query q = em.createQuery("SELECT m from Medical m where m.seafarerId=:seafarerId order by m.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		try{
			return (Medical) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}

	}
}
