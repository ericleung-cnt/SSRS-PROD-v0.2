package org.mardep.ssrs.dao.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.user.TransactionLock;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionLockJpaDao extends AbstractJpaDao<TransactionLock, String> implements ITransactionLockDao {
	private List<PredicateCriteria> criteriaList = new ArrayList<>();

	public TransactionLockJpaDao() {
		criteriaList.add(new PredicateCriteria("applNo", PredicateType.EQUAL));
	}
	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb, Root<TransactionLock> listRoot) {
		return criteriaList;
	}
}
