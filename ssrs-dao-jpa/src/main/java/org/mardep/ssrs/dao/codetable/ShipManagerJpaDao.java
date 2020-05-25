package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.ShipManager;
import org.springframework.stereotype.Repository;

@Repository
public class ShipManagerJpaDao extends AbstractJpaDao<ShipManager, Long> implements IShipManagerDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<ShipManager> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("shipMgrName", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("addr1", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("addr2", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("addr3", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("companyId", PredicateType.EQUAL));
		list.add(new PredicateCriteria("email", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}

	@Override
	public ShipManager findByName(String name) {
		try{
			StringBuffer sb = new StringBuffer();
			sb.append("select sm from ShipManager sm where sm.shipMgrName =:shipMgrName ");
			Query query = em.createQuery(sb.toString());
			query.setParameter("shipMgrName", name);
			//query.setMaxResults(1);
			//ShipManager entity =  (ShipManager) query.getSingleResult();
			List<ShipManager> entities = query.getResultList();
			ShipManager entity = entities.get(0);
			return entity;
		}catch(Exception ex){
			return null;
		}
	}
	
	@Override
	public List<ShipManager> getAll() {
		try {
			Query query = em.createQuery("select s from ShipManager order by s.shipMgrName", ShipManager.class);
			List<ShipManager> lst = query.getResultList();
			return lst;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		}
	}
}
