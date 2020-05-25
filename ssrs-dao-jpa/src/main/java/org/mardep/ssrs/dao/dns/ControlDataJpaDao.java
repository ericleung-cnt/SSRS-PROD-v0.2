package org.mardep.ssrs.dao.dns;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.dns.ControlAction;
import org.mardep.ssrs.domain.dns.ControlData;
import org.mardep.ssrs.domain.dns.ControlEntity;
import org.springframework.stereotype.Repository;

@Repository
public class ControlDataJpaDao extends AbstractJpaDao<ControlData, Long> implements IControlDataDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(
			CriteriaBuilder cb, Root<ControlData> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("entityId", PredicateType.LIKE));
		list.add(new PredicateCriteria("entity", PredicateType.EQUAL));
		list.add(new PredicateCriteria("action", PredicateType.EQUAL));
		list.add(new PredicateCriteria("processed", PredicateType.EQUAL));
		list.add(new PredicateCriteria("processedDateFrom", "processedDate", PredicateType.GREATER_OR_EQUAL));
		list.add(new PredicateCriteria("processedDateTo", "processedDate", PredicateType.LESS_OR_EQUAL));
		list.add(new PredicateCriteria("error", PredicateType.EQUAL));
		list.add(new PredicateCriteria("errorDateFrom", "errorDate", PredicateType.GREATER_OR_EQUAL));
		list.add(new PredicateCriteria("errorDateTo", "errorDate", PredicateType.LESS_OR_EQUAL));
		return list;
	}
	
	@Override
	public ControlData readOne() {
		try{
			Query q = em.createQuery(" select c from ControlData c where c.processed = 0 order by c.createdDate");
			q.setMaxResults(1);
			return (ControlData)q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ControlData> readForScheduler() {
		Query query = em.createQuery(" select c from ControlData c where c.processed = 0 order by c.createdDate ");
		List<ControlData> results = query.getResultList();
		return results;
	}

	@Override
	public Long findCountBy(ControlEntity ce, ControlAction ca, String entityId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select count(*) from ControlData c ");
		sb.append(" where c.entityId = :entityId ");
		sb.append(" and c.action = :action ");
		sb.append(" and c.entity = :entity ");
		Query query = em.createQuery(sb.toString());
		query.setParameter("entityId", entityId);
		query.setParameter("action", ca.getCode());
		query.setParameter("entity", ce.getCode());
		Long results = (Long) query.getSingleResult();
		return results;
	}

	
	@Override
	public ControlData find(ControlEntity ce, ControlAction ca, String entityId){
		try{
			Query query = em.createQuery(" select c from ControlData c where c.entity =:entity and c.action=:action and c.entityId= :entityId order by c.createdDate");
			query.setParameter("entityId", entityId);
			query.setParameter("action", ca.getCode());
			query.setParameter("entity", ce.getCode());
			query.setMaxResults(1);
			return (ControlData)query.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
	}
	 
}
