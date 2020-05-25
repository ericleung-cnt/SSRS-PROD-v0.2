package org.mardep.ssrs.dao.dn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.constant.CwIndicator;
import org.mardep.ssrs.domain.constant.ReceiptStatus;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.springframework.stereotype.Repository;

@Repository
public class DemandNoteReceiptJpaDao extends AbstractJpaDao<DemandNoteReceipt, Long> implements IDemandNoteReceiptDao {

	public DemandNoteReceiptJpaDao() {
		criteriaList.add(new PredicateCriteria("demandNoteNo", PredicateType.EQUAL));
	}

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<DemandNoteReceipt> listRoot) {
		listRoot.fetch("demandNoteHeader", JoinType.LEFT);
		return criteriaList;
	}

	@Override
	public List<Object[]> findReceiptCollected01(Date from, Date to, String sortBy) {
		Query query = em.createNativeQuery("select "
				+ "r.dn_demand_note_no, h.generation_time, h.due_date, h.payment_status, r.input_time, "
				+ "r.receipt_no, h.bill_name, h.amount hamount, r.amount ramount, case when r.can_adj_status is not null or r.cancel_by is not null then 'Y' else 'N' end isCancel, "
				+ "fc.form_code, h.ebs_flag, i.fc_fee_code, i.charged_units, i.amount iamount, r.payment_type "
				+ "from demand_note_receipts r inner join demand_note_headers h on h.demand_note_no = r.dn_demand_note_no "
				+ "inner join demand_note_items i on h.demand_note_no = i.dn_demand_note_no "
				//+ "inner join (select min(item_id) item_id, dn_demand_note_no from demand_note_items group by dn_demand_note_no) firstitem on i.dn_demand_note_no = firstitem.dn_demand_note_no and i.item_id = firstitem.item_id "
				+ "inner join fee_codes fc on fc.FEE_CODE = i.fc_fee_code "
				+ "where "
				// payment status "Oustanding" were captured
				+ "h.payment_status <> '0' "
				+ "and r.STATUS <> 'P' and r.input_time >= :from and r.input_time <= :to "
				+ "order by r.dn_demand_note_no, " + ("inputTime".equals(sortBy) ? "r.input_time" : "status".equals(sortBy) ? "r.status" : "h.generation_time"));
		query.setParameter("from", from);
		query.setParameter("to", to);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteReceipt> findException(Date from, Date to){
//		PAY_STATUS = 2
//				or PAY_STATUS = 3
//				or (PAY_STATUS = 1 and DN_STATUS = 11)
//				or (PAY_STATUS = 3 and DN_STATUS = 11)
//				or (PAY_STATUS > 0 and DN_STATUS = 12)
//				or (PAY_STATUS = 0 and DN_STATUS = 16)
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT dnr from DemandNoteReceipt dnr left join fetch dnr.demandNoteHeader dnh where dnh.generationTime>=:from and dnh.generationTime<=:to and ");
		sb.append(" (");
//		sb.append(" dnh.paymentStatus <> '1' ");
		sb.append("    dnh.paymentStatus = '2' ");
		sb.append(" or dnh.paymentStatus = '3' ");
		sb.append(" or (dnh.paymentStatus = '1' and dnh.status = '11') ");
		sb.append(" or (dnh.paymentStatus = '3' and dnh.status = '11') ");
		sb.append(" or (dnh.paymentStatus = '0' and dnh.status = '12') ");
		sb.append(" or (dnh.paymentStatus = '0' and dnh.status = '16') ");
		sb.append(" )");
		sb.append(" order by dnr.demandNoteNo ");
		Query q = em.createQuery(sb.toString());
//		Query q = em.createQuery("SELECT dnr from DemandNoteReceipt dnr left join fetch dnr.demandNoteHeader dnh where dnh.generationTime>=:from and dnh.generationTime<=:to and dnh.paymentStatus <> '1' order by dnr.demandNoteNo");
		q.setParameter("from", from);
		q.setParameter("to", to);
		return q.getResultList();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteReceipt> findCancelled(Date from, Date to){
		List<String> status = new ArrayList<String>();
		status.add(CwIndicator.W.getCode());
		status.add(CwIndicator.C.getCode());
//		status.add(DemandNoteHeader.STATUS_CANCELLED);
//		status.add(DemandNoteHeader.STATUS_WRITTEN_OFF);
		Query q = em.createQuery("SELECT dnr from DemandNoteReceipt dnr left join fetch dnr.demandNoteHeader dnh where dnh.cwStatus IN :inclList and dnh.generationTime>=:from and dnh.generationTime<=:to order by dnr.demandNoteNo");
		q.setParameter("inclList",status);
		q.setParameter("from", from);
		q.setParameter("to", to);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteReceipt> findByDemandNoteNo(String demandNoteNo){
		Query q = em.createQuery("SELECT dnr from DemandNoteReceipt dnr where dnr.demandNoteNo=:demandNoteNo order by dnr.receiptId");
		q.setParameter("demandNoteNo", demandNoteNo);
		return q.getResultList();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteReceipt> findValue(String demandNoteNo){
		Query q = em.createQuery("SELECT dnr from DemandNoteReceipt dnr where dnr.demandNoteNo=:demandNoteNo and dnr.inputTime<=:valueDate order by dnr.receiptId");
		q.setParameter("demandNoteNo", demandNoteNo);
		q.setParameter("valueDate", new Date(), TemporalType.TIMESTAMP);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteReceipt> findPending(){
		Query q = em.createQuery("SELECT dnr from DemandNoteReceipt dnr left join fetch dnr.demandNoteHeader where dnr.status=:status and dnr.inputTime <= :valueDate ");
		q.setParameter("status", ReceiptStatus.PENDING.getCode());
		q.setParameter("valueDate", new Date());
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteReceipt> findByReceiptNo(String receiptNo){
		Query q = em.createQuery("SELECT dnr from DemandNoteReceipt dnr where dnr.receiptNo=:receiptNo order by dnr.receiptId");
		q.setParameter("receiptNo", receiptNo);
		return q.getResultList();
	}

}
