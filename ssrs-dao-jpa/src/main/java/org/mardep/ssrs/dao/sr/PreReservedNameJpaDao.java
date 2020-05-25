package org.mardep.ssrs.dao.sr;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.domain.sr.PreReservedName;
import org.springframework.stereotype.Repository;

@Repository
public class PreReservedNameJpaDao extends AbstractJpaDao<PreReservedName, Long> implements IPreReservedNameDao {

	@Override
	public List<PreReservedName> isReserved(List<String> names, boolean eng) {
		Query query = em.createQuery("SELECT p from PreReservedName p where p.name in (:names) "
				+ "and p.expiryTime > current_date() "
				+ "and p.releaseTime = null "
				+ "and p.language = '" + (eng ? PreReservedName.LANG_EN : PreReservedName.LANG_ZH)
				+ "' "
				+ "");
		query.setParameter("names", names);
		return query.getResultList();
	}

	@Override
	public List<PreReservedName> findPreReserveApps() {
		String query = "select p from PreReservedName p "
				+ "where p.releaseReason = null and p.expiryTime = null";
		return em.createQuery(query).getResultList();

	}

	@Override
	public List<PreReservedName> findNames(String name, String language) {
		String querystr = "select p from PreReservedName p "
				+ "where p.name = :name and p.language = :language ";

		Query query = em.createQuery(querystr);
		query.setParameter("name", name);
		query.setParameter("language", language);

		return query.getResultList();
	}
	@Override
	protected void resolveConditionList(Map<String, Criteria> map, CriteriaBuilder cb,
			List<Predicate> countPredicateList, Root<PreReservedName> countRoot, List<Predicate> listPredicateList,
			Root<PreReservedName> listRoot) {
		super.resolveConditionList(map, cb, countPredicateList, countRoot, listPredicateList, listRoot);
		addDefault(cb, countPredicateList, countRoot, listPredicateList, listRoot);
	}

	@Override
	protected void resolveConditionList(List<PredicateCriteria> list, Map<String, Criteria> map, CriteriaBuilder cb,
			List<Predicate> countPredicateList, Root<PreReservedName> countRoot, List<Predicate> listPredicateList,
			Root<PreReservedName> listRoot) {
		super.resolveConditionList(list, map, cb, countPredicateList, countRoot, listPredicateList, listRoot);
		addDefault(cb, countPredicateList, countRoot, listPredicateList, listRoot);
	}

	private void addDefault(CriteriaBuilder cb, List<Predicate> countPredicateList, Root<PreReservedName> countRoot,
			List<Predicate> listPredicateList, Root<PreReservedName> listRoot) {
		Predicate release = cb.or(cb.isNull(countRoot.get("releaseTime")), cb.greaterThanOrEqualTo(getPath(countRoot, new String[]{"releaseTime"}), new Date()));
		Predicate exp = cb.greaterThanOrEqualTo(getPath(countRoot, new String[]{"expiryTime"}), new Date());
		countPredicateList.add(release);
		countPredicateList.add(exp);
		Predicate release2 = cb.or(cb.isNull(listRoot.get("releaseTime")), cb.greaterThanOrEqualTo(getPath(listRoot, new String[]{"releaseTime"}), new Date()));
		Predicate exp2 = cb.greaterThanOrEqualTo(getPath(listRoot, new String[]{"expiryTime"}), new Date());
		listPredicateList.add(release2);
		listPredicateList.add(exp2);
	}
}
