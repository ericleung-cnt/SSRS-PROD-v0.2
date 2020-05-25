package org.mardep.ssrs.dao.seafarer;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.seafarer.Seafarer;
import org.springframework.stereotype.Repository;

@Repository
public class SeafarerJpaDao extends AbstractJpaDao<Seafarer, String> implements ISeafarerDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Seafarer> listRoot) {
		listRoot.fetch("nationality", JoinType.LEFT);
		listRoot.fetch("rating", JoinType.LEFT);
		listRoot.fetch("previousSerb", JoinType.LEFT);
		listRoot.fetch("ratings", JoinType.LEFT);
		listRoot.fetch("previousSerbs", JoinType.LEFT);
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("idEqual", "id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("partType", PredicateType.EQUAL));
		list.add(new PredicateCriteria("sex", PredicateType.EQUAL));
		list.add(new PredicateCriteria("surname", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("firstName", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("chiName", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("serbNo", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("serbNoEqual", "serbNo", PredicateType.EQUAL));
		list.add(new PredicateCriteria("province", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("nationalityId", PredicateType.EQUAL));
		list.add(new PredicateCriteria("rankId", "rating/capacityId", PredicateType.EQUAL));
		list.add(new PredicateCriteria("previousSerbNo", "previousSerb/serbNo", PredicateType.LIKE_IGNORE_CASE));
		return list;
	}
	
	protected String orderFieldMap(String key){
		if("nationalityEngDesc".equals(key)){
			return "nationality/engDesc";
		}
		return null;
	}

	@Override
	public Seafarer findByIdSerbNo(String id, String serbNo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Seafarer> cq = cb.createQuery(Seafarer.class);
		Root<Seafarer> from = cq.from(Seafarer.class);
		List<Predicate> list = new ArrayList<Predicate>();
		if(id!=null && !id.isEmpty()){
			list.add(cb.equal(from.get("idNo"), id));
		}
		if(serbNo!=null && !serbNo.isEmpty()){
			list.add(cb.equal(from.get("serbNo"), serbNo));
		}
		cq.where(list.toArray(new Predicate[list.size()]));
		TypedQuery<Seafarer> query = em.createQuery(cq);
		
		try{
			return query.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
	}
	
	@Override
	public Seafarer findBySerbNo(String serbNo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Seafarer> cq = cb.createQuery(Seafarer.class);
		Root<Seafarer> from = cq.from(Seafarer.class);
		List<Predicate> list = new ArrayList<Predicate>();
		if(serbNo!=null && !serbNo.isEmpty()){
			list.add(cb.equal(from.get("serbNo"), serbNo));
		}
		cq.where(list.toArray(new Predicate[list.size()]));
		TypedQuery<Seafarer> query = em.createQuery(cq);
		
		try{
			return query.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
	}
}
