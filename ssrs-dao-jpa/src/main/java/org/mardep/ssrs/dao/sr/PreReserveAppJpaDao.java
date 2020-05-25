package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.PreReserveApp;
import org.springframework.stereotype.Repository;

@Repository
public class PreReserveAppJpaDao extends AbstractJpaDao<PreReserveApp, Long> implements IPreReserveAppDao {

	public PreReserveAppJpaDao() {
		criteriaList.add(new PredicateCriteria("id", PredicateType.EQUAL));
	}

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb, Root<PreReserveApp> listRoot) {
		return criteriaList;
	}

	@Override
	public List<PreReserveApp> findPendingApps(Long id) {
		String qlString = "select app from PreReserveApp app where ";
		if (id != null) {
			qlString += "id = :id ";
		} else {
			qlString += "app.reserveTime = null and app.rejectTime = null ";
		}
		Query query = em.createQuery(qlString);
		if (id != null) {
			query.setParameter("id", id);
		}
		return query.getResultList();
	}

}
