package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.SdData;
import org.springframework.stereotype.Repository;

@Repository
public class SdDataJpaDao extends AbstractJpaDao<SdData, String> implements ISdDataDao {
	private List<PredicateCriteria> criteria = new ArrayList<>();

	public SdDataJpaDao() {
		criteria.add(new PredicateCriteria("imoNo", PredicateType.EQUAL));
	}

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb,
			Root<SdData> listRoot) {
		return criteria;
	}
}
