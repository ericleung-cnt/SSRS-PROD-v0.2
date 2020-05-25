package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.DocumentRemark;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentRemarkJpaDao extends AbstractJpaDao<DocumentRemark, String> implements IDocumentRemarkDao {
	
	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<DocumentRemark> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("remark", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}

	@Override
	public List<DocumentRemark> getAll() {
		// TODO Auto-generated method stub
		try {
				Query query = em.createQuery("select r from DocumentRemark r");
				List<DocumentRemark> remarkList = (List<DocumentRemark>) query.getResultList();
				return remarkList;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<DocumentRemark> getByGroup(String group) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select r from DocumentRemark r where r.remarkGroup=:group")
					.setParameter("group", group);
			List<DocumentRemark> remarkList = (List<DocumentRemark>) query.getResultList();
			return remarkList;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
