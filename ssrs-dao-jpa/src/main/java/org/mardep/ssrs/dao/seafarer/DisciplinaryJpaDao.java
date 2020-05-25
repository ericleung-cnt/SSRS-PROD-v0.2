package org.mardep.ssrs.dao.seafarer;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.Disciplinary;
import org.springframework.stereotype.Repository;

@Repository
public class DisciplinaryJpaDao extends AbstractJpaDao<Disciplinary, CommonPK> implements IDisciplinaryDao {

	public DisciplinaryJpaDao() {
		criteriaList.add(new PredicateCriteria("seafarerId", 		PredicateType.EQUAL));
		criteriaList.add(new PredicateCriteria("seqNo", 		PredicateType.EQUAL));
		criteriaList.add(new PredicateCriteria("recordDate", 	PredicateType.EQUAL));
	}

	@Override
	public Disciplinary findLatestBySeafarerId(String seafarerId){
		Query q = em.createQuery("SELECT d from Disciplinary d where d.seafarerId=:seafarerId order by d.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		try{
			return (Disciplinary) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}

	}
}
