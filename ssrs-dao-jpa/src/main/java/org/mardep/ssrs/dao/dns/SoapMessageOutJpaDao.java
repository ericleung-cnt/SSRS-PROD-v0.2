package org.mardep.ssrs.dao.dns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.dns.SoapMessageOut;
import org.springframework.stereotype.Repository;

@Repository
public class SoapMessageOutJpaDao extends AbstractJpaDao<SoapMessageOut, Long> implements ISoapMessageOutDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(
			CriteriaBuilder cb, Root<SoapMessageOut> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("controlId", PredicateType.LIKE));
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
//		list.add(new PredicateCriteria("read", PredicateType.EQUAL));
		list.add(new PredicateCriteria("error", PredicateType.EQUAL));
		list.add(new PredicateCriteria("processedDateFrom", "processedDate", PredicateType.GREATER_OR_EQUAL));
		list.add(new PredicateCriteria("processedDateTo", "processedDate", PredicateType.LESS_OR_EQUAL));
//		list.add(new PredicateCriteria("readDateFrom", "readDate", PredicateType.GREATER_OR_EQUAL));
//		list.add(new PredicateCriteria("readDateTo", "readDate", PredicateType.LESS_OR_EQUAL));
		list.add(new PredicateCriteria("errorDateFrom", "errorDate", PredicateType.GREATER_OR_EQUAL));
		list.add(new PredicateCriteria("errorDateTo", "errorDate", PredicateType.LESS_OR_EQUAL));
		return list;
	}
	
	
	@Override
	public SoapMessageOut readOne(){
		StringBuffer sb = new StringBuffer();
		sb.append("select s from SoapMessageOut s ");
		sb.append("where s.processed = 0 ");
		Query query = em.createQuery(sb.toString());
		query.setMaxResults(1);
		SoapMessageOut o = (SoapMessageOut) query.getSingleResult();
		return o;

	}

	@Override
	public SoapMessageOut findByControlId(Long controlId){
		StringBuffer sb = new StringBuffer();
		sb.append(" select s from SoapMessageOut s where s.controlId =:controlId ");
		sb.append(" order by s.createdDate desc ");
		TypedQuery<SoapMessageOut> query = em.createQuery(sb.toString(), SoapMessageOut.class);
		query.setParameter("controlId", controlId);
		List<SoapMessageOut> resultList = query.getResultList();
		if (resultList.size() > 0){
			return resultList.get(0);
		}
		return null;
	}

	@Override
	public List<SoapMessageOut> findListByControlId(Long controlId){
		StringBuffer sb = new StringBuffer();
		sb.append("select s from SoapMessageOut s where s.controlId =:controlId");
		TypedQuery<SoapMessageOut> query = em.createQuery(sb.toString(), SoapMessageOut.class);
		query.setParameter("controlId", controlId);
		List<SoapMessageOut> resultList = query.getResultList();
		return resultList;
	}

	@Override
	public List<SoapMessageOut> findResponseAfter(Date date) {
		Query query = em.createQuery("select message from SoapMessageOut message "
				+ "where message.response is not null "
				+ "and message.processedDate >= :date ");
		query.setParameter("date", date);

		return query.getResultList();
	}
}
