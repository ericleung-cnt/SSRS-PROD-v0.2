package org.mardep.ssrs.dao.seafarer;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.seafarer.SeafarerSearch;
import org.springframework.stereotype.Repository;

@Repository
public class SeafarerSearchJpaDao extends AbstractJpaDao<SeafarerSearch, String> implements ISeafarerSearchDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<SeafarerSearch> listRoot) {
		listRoot.fetch("nationality", JoinType.LEFT);
		listRoot.fetch("rating", JoinType.LEFT);
		listRoot.fetch("previousSerb", JoinType.LEFT);
		listRoot.fetch("ratings", JoinType.LEFT);
		listRoot.fetch("previousSerbs", JoinType.LEFT);
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("idEqual", "id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("idNo", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("idNoEqual", "idNo", PredicateType.EQUAL));
		list.add(new PredicateCriteria("partType", PredicateType.EQUAL));
		list.add(new PredicateCriteria("sex", PredicateType.EQUAL));
		list.add(new PredicateCriteria("surname", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("firstName", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("chiName", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("serbNo", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("serbNoEqual", "serbNo", PredicateType.EQUAL));
		list.add(new PredicateCriteria("province", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("mobile", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("telephone", PredicateType.LIKE_IGNORE_CASE));
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
	 
}
