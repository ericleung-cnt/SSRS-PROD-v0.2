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
import org.mardep.ssrs.domain.seafarer.Cert;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.springframework.stereotype.Repository;

@Repository
public class CertJpaDao extends AbstractJpaDao<Cert, CommonPK> implements ICertDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Cert> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("seqNo", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("seafarerId", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("certType", 		PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("issueDate", 	PredicateType.EQUAL));
		list.add(new PredicateCriteria("issueAuthority", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("certNo",		PredicateType.LIKE_IGNORE_CASE));

		return list;
	}
	
	@Override
	public Cert findLatestBySeafarerId(String seafarerId){
		Query q = em.createQuery("SELECT c from Cert c where c.seafarerId=:seafarerId order by c.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		try{
			return (Cert) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}

	}
}
