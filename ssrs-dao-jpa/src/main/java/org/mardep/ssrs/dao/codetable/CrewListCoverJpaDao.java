package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.CrewListCover;
import org.mardep.ssrs.domain.codetable.CrewListCoverPK;
import org.springframework.stereotype.Repository;

@Repository
public class CrewListCoverJpaDao extends AbstractJpaDao<CrewListCover, CrewListCoverPK> implements ICrewListCoverDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<CrewListCover> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("vesselId", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("coverYymm", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("imoNo", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("agreementPeriod", PredicateType.EQUAL));
		list.add(new PredicateCriteria("commenceDateTo", 	"commenceDate", PredicateType.LESS_OR_EQUAL));
		list.add(new PredicateCriteria("commenceDateFrom", 	"commenceDate", PredicateType.GREATER_OR_EQUAL));
		list.add(new PredicateCriteria("terminateDateTo", 	"terminateDate", PredicateType.LESS_OR_EQUAL));
		list.add(new PredicateCriteria("terminateDateFrom", 	"terminateDate", PredicateType.GREATER_OR_EQUAL));
		return list;
	}
	
}