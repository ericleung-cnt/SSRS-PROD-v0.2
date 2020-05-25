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
import org.mardep.ssrs.domain.seafarer.SeaService;
import org.springframework.stereotype.Repository;

@Repository
public class SeaServiceJpaDao extends AbstractJpaDao<SeaService, CommonPK> implements ISeaServiceDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<SeaService> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("seqNo", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("seafarerId", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("vesselName", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("voyageType", 	PredicateType.EQUAL));
		list.add(new PredicateCriteria("flag", 			PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("shipTypeCode", 	PredicateType.EQUAL));
		list.add(new PredicateCriteria("capacity", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("employmentDate", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("empoymentPlace", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("dischargeDate", 		PredicateType.EQUAL));
		list.add(new PredicateCriteria("dischargePlace", 	PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("reportDate", 	PredicateType.EQUAL));
		list.add(new PredicateCriteria("dchpend", 	PredicateType.EQUAL));
		list.add(new PredicateCriteria("diDate", 	PredicateType.EQUAL));

		return list;
	}
	
	@Override
	public SeaService findLatestBySeafarerId(String seafarerId){
		Query q = em.createQuery("SELECT ss from SeaService ss where ss.seafarerId=:seafarerId order by ss.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setMaxResults(1);
		try{
			return (SeaService) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SeaService> findBySeafarerId(String seafarerId){
		Query q = em.createQuery("SELECT ss from SeaService ss where ss.seafarerId=:seafarerId order by ss.employmentDate desc");
		q.setParameter("seafarerId", seafarerId);
		try{
			return q.getResultList();
		}catch (Exception nre){
			return Collections.emptyList();
		}
		
	}

	@Override
	public SeaService findPreviousBySeafarerId(String seafarerId, Integer seqNo){
		Query q = em.createQuery("SELECT ss from SeaService ss where ss.seafarerId=:seafarerId and ss.seqNo < :seqNo order by ss.seqNo desc");
		q.setParameter("seafarerId", seafarerId);
		q.setParameter("seqNo", seqNo);
		q.setMaxResults(1);
		try{
			return (SeaService) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
	}
}
