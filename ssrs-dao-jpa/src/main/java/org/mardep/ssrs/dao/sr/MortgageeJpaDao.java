package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.domain.sr.MortgageePK;
import org.springframework.stereotype.Repository;

@Repository
public class MortgageeJpaDao extends AbstractJpaDao<Mortgagee, MortgageePK> implements IMortgageeDao {
	private List<PredicateCriteria> criteriaList = new ArrayList<PredicateCriteria>();

	public MortgageeJpaDao() {
		criteriaList.add(new PredicateCriteria("applNo", PredicateType.EQUAL));
		criteriaList.add(new PredicateCriteria("priorityCode", PredicateType.EQUAL));
	}

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb, Root<Mortgagee> listRoot) {
		return criteriaList;
	}

	@Override
	public List<Mortgagee> findByCriteria(Mortgage entity) {
		Mortgagee criteria = new Mortgagee();
		criteria.setApplNo(entity.getApplNo());
		criteria.setApplNo(entity.getPriorityCode());
		return findByCriteria(criteria);
	}

	@Override
	public List<Mortgagee> findByTime(Mortgage mortgage, Date reportDate) {
		List<Mortgagee> result = new ArrayList<>();
		List<Mortgagee> list = findHistory(mortgage.getApplNo(), reportDate);
		for (Mortgagee m : list) {
			if (mortgage.getPriorityCode().equals(m.getPriorityCode())) {
				result.add(m);
			}
		}
		return result;
	}

	@Override
	public List<Mortgagee> findByMortgage(Mortgage mortgage) {
		// TODO Auto-generated method stub
		List<Mortgagee> mortgageeList = findByCriteria(mortgage);
		return mortgageeList;
	}

}
