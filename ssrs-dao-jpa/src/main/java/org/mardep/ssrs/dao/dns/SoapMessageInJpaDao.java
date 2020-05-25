package org.mardep.ssrs.dao.dns;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.dns.SoapMessageIn;
import org.springframework.stereotype.Repository;

@Repository
public class SoapMessageInJpaDao extends AbstractJpaDao<SoapMessageIn, Long> implements ISoapMessageInDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(
			CriteriaBuilder cb, Root<SoapMessageIn> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("sent", PredicateType.EQUAL));
		list.add(new PredicateCriteria("error", PredicateType.EQUAL));
		list.add(new PredicateCriteria("sentDateFrom", "sentDate", PredicateType.GREATER_OR_EQUAL));
		list.add(new PredicateCriteria("sentDateTo", "sentDate", PredicateType.LESS_OR_EQUAL));
		list.add(new PredicateCriteria("errorDateFrom", "errorDate", PredicateType.GREATER_OR_EQUAL));
		list.add(new PredicateCriteria("errorDateTo", "errorDate", PredicateType.LESS_OR_EQUAL));
		return list;
	}
	
}
