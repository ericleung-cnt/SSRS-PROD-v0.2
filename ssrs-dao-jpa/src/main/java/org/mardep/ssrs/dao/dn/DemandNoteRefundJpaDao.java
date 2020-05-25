package org.mardep.ssrs.dao.dn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.springframework.stereotype.Repository;

@Repository
public class DemandNoteRefundJpaDao extends AbstractJpaDao<DemandNoteRefund, Long> implements IDemandNoteRefundDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<DemandNoteRefund> listRoot) {
		listRoot.fetch("demandNoteHeader", JoinType.LEFT);
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
//		list.add(new PredicateCriteria("itemNo", PredicateType.EQUAL));
		list.add(new PredicateCriteria("demandNoteNo", PredicateType.EQUAL));
		list.add(new PredicateCriteria("refundId", PredicateType.EQUAL));
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteRefund> findByDemandNoteNo(String demandNoteNo){
		Query q = em.createQuery("SELECT distinct dnr from DemandNoteRefund dnr where dnr.demandNoteNo=:demandNoteNo order by dnr.refundId");
		q.setParameter("demandNoteNo", demandNoteNo);
		return q.getResultList();
	}

	/**
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DemandNoteRefund> findRefund(Date from, Date to, Date receiptDate){
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT distinct dnr from DemandNoteRefund dnr ");
		sb.append(" left join fetch dnr.demandNoteHeader dnh   ");
		sb.append(" where dnh.status=:dnStatus and dnr.voucherDate>=:from and dnr.voucherDate<=:to ");
//		if(receiptDate!=null){
//			sb.append("  ");
//		}
		sb.append(" order by dnr.demandNoteNo ");
		Query q = em.createQuery(sb.toString());
//		Query q = em.createQuery("SELECT distinct dnr from DemandNoteRefund dnr left join fetch dnr.demandNoteHeader dnh  where dnh.status=:dnStatus and dnr.voucherDate>=:from and dnr.voucherDate<=:to order by dnr.demandNoteNo");
		q.setParameter("from", from);
		q.setParameter("to", to);
//		if(receiptDate!=null){
//			q.setParameter("to", to);
//		}
		q.setParameter("dnStatus", DemandNoteHeader.STATUS_REFUNDED);
		return q.getResultList();
	}

}
