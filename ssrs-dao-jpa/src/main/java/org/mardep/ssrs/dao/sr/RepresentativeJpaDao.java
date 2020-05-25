package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.Representative;
import org.springframework.stereotype.Repository;

@Repository
public class RepresentativeJpaDao extends AbstractJpaDao<Representative, String> implements IRepresentativeDao {
	private List<PredicateCriteria> criteriaList = new ArrayList<>();

	public RepresentativeJpaDao() {
		criteriaList.add(new PredicateCriteria("applNo", PredicateType.EQUAL));
	}
	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb, Root<Representative> listRoot) {
		return criteriaList;
	}
	@Override
	public Representative findByApplId(String applNo, Date reportDate) {
		List<Representative> rep = findHistory(applNo, reportDate);
		return rep.isEmpty() ? null : (Representative) rep.get(0);
	}
	
	@Override
	public Representative findByApplId(String applNo) {
		Query query = em.createQuery("select rp from Representative rp where rp.applNo = :applNo");
		query.setParameter("applNo", applNo);
		List<Representative> rpList = query.getResultList();
		return rpList.isEmpty() ? null : (Representative) rpList.get(0);
	}
}
