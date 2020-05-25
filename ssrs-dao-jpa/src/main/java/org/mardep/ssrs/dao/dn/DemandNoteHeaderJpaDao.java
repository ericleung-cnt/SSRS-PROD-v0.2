package org.mardep.ssrs.dao.dn;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.springframework.stereotype.Repository;

@Repository
public class DemandNoteHeaderJpaDao extends AbstractJpaDao<DemandNoteHeader, String> implements IDemandNoteHeaderDao {
	public DemandNoteHeaderJpaDao() {
		super();
		for (int i = 0; i < criteriaList.size(); i++) {
			PredicateCriteria c = criteriaList.get(i);
			switch (c.getKey()) {
			case "demandNoteNo":
			case "billName":
			case "coName1":
			case "address1":
				PredicateCriteria element = new PredicateCriteria(c.getKey(), PredicateType.LIKE_IGNORE_CASE);
				criteriaList.set(i, element);
				break;
			}
		}
		criteriaList.add(new PredicateCriteria("generationTimeFrom", "generationTime", PredicateType.GREATER_OR_EQUAL));
		criteriaList.add(new PredicateCriteria("generationTimeTo", 	"generationTime", PredicateType.LESS_OR_EQUAL));
	}

	@Override
	public Long findNextId() {
		Query query = em.createNativeQuery("select next value for dbo.DEMAND_NOTE_ID ");
		return ((BigInteger) query.getSingleResult()).longValue();
	}

	@Override
	public List<DemandNoteHeader> findSrDn(Map criteria, long start, long end) {
		return srDnFrom(criteria, "dh").setFirstResult((int)start).setMaxResults((int)(end - start)).getResultList();
	}
	@Override
	public long countSrDn(Map criteria) {
		return (long) srDnFrom(criteria, "count(*)").getResultList().get(0);
	}

	private Query srDnFrom(Map criteria, String select) {
		StringBuilder buffer = new StringBuilder("select ");
		buffer.append(select);
		buffer.append(" from DemandNoteHeader dh where dh.applNo <> '0' ");
		boolean advanced = "AdvancedCriteria".equals(criteria.get("_constructor"));
		List criteriaList = (List) criteria.get("criteria");
		if(advanced) {
			String operator = (String) criteria.get("operator");
			if (criteriaList != null) {
				for (Object c : criteriaList) {
					buffer.append(operator).append(" ");
					Map condition = (Map) c;
					buffer.append((String) condition.get("fieldName"));
					String op = (String) condition.get("operator");
					switch (op) {
					case "iContains":
						buffer.append(" like :").append(condition.get("fieldName")).append(" ");
						break;
					case "greaterOrEqual":
						buffer.append(" >= :").append(condition.get("fieldName")).append("GE ");
						break;
					case "lessOrEqual":
						buffer.append(" <= :").append(condition.get("fieldName")).append("LE ");
						break;
					default:
						buffer.append(" = :").append(condition.get("fieldName")).append(" ");
						break;
					}

				}
			}
		} else {
			for (Object key : criteria.keySet()) {//{demandNoteNo=00000, applNo=99/}
				buffer.append(" and ").append(key).append(" like :").append(key);
			}
		}
		Query query = em.createQuery(buffer.toString());
		if(advanced) {
			if (criteriaList != null) {
				for (Object c : criteriaList) {
					Map condition = (Map) c;
					String op = (String) condition.get("operator");
					switch (op) {
					case "iContains":
						query.setParameter((String) condition.get("fieldName"), "%" + condition.get("value") + "%");
						break;
					case "greaterOrEqual":
						query.setParameter(condition.get("fieldName")+"GE", condition.get("value"));
						break;
					case "lessOrEqual":
						query.setParameter(condition.get("fieldName")+"LE", condition.get("value"));
						break;
					default:
						query.setParameter((String) condition.get("fieldName"), condition.get("value"));
						break;
					}
				}
			}
		} else {
			for (Object key : criteria.keySet()) {
				query.setParameter((String) key, "%"+criteria.get(key) +"%");
			}
		}
		return query;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<DemandNoteHeader> listForReminder(String id) {
		Query query = em.createNativeQuery("select " +
				"dnh.RM_APPL_NO, dnh.DEMAND_NOTE_NO, dnh.AMOUNT, " +
				"dnh.DUE_DATE, dnh.CW_REMARK, dnh.AMOUNT_PAID, " +
				"dnh.CO_NAME, dnh.BILL_NAME " +
				"from demand_note_headers dnh,SYSTEM_PARAMS sp " +
				"where dnh.demand_note_status = '3' and dnh.payment_status in ('0', '2') and dnh.cw_time is null and dnh.RM_APPL_NO <> '0' " +
				"and sp.id = :id " +
				"and format(dnh.DUE_DATE, 'yyyyMMdd') " +
				"= format( " +
				"   DATEADD(day,-cast(cast(value as varchar) as integer),format(getdate(),'yyyy-MM-dd')), " +
				"   'yyyyMMdd' " +
				") " +
				"and " +
				"( " +
				"   not exists " +
				"   ( " +
				"      select " +
				"      1 " +
				"      from demand_note_receipts dnr " +
				"      where dnr.dn_demand_note_no = dnh.demand_note_no " +
				"   ) " +
				"   or not exists " +
				"   ( " +
				"      select " +
				"      1 " +
				"      from " +
				"      ( " +
				"        select DN_DEMAND_NOTE_NO, sum(AMOUNT) paid " +
				"        from (select  DN_DEMAND_NOTE_NO, AMOUNT from demand_note_receipts " +
				"        where CANCEL_DATE is null and status = 'I'  " +
				"        union " +
				"        select DN_DEMAND_NOTE_NO, -AMOUNT " +
				"        from demand_note_refunds where REFUND_STATUS = 'APPROVED') DNRR " +
				"        group by DN_DEMAND_NOTE_NO " +
				"      ) " +
				"      paid " +
				"      where paid.dn_demand_note_no = dnh.demand_note_no " +
				"      and (paid.paid >= dnh.amount) " +
				"   ) " +
				") ");
		query.setParameter("id", id);
		List<DemandNoteHeader> result = new ArrayList<>();
		List list = query.getResultList();
		for (Object row : list) {
			Object[] array = (Object[]) row;
			DemandNoteHeader header = new DemandNoteHeader();
			header.setApplNo((String) array[0]);
			header.setDemandNoteNo((String) array[1]);
			header.setAmount((BigDecimal) array[2]);
			header.setDueDate((Date) array[3]);
			result.add(header);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteHeader> findAge(String sortBy, Integer overDueTimeFrame,  Date on){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT distinct dnh from DemandNoteHeader dnh ");
		sb.append(" where dnh.paymentStatus=:oustanding" );
		sb.append(" and dnh.generationTime < :on and dnh.cwStatus is null and dnh.amount > 0 and dnh.status <> '16' ");
		Date comparedStart = null;
		Date comparedEnd = null;
		if(overDueTimeFrame!=null){
			if(overDueTimeFrame.intValue()==30){
				comparedStart = DateUtils.addDays(on, -30);
				comparedEnd = DateUtils.addDays(on, 1);
			}else if(overDueTimeFrame.intValue()==60){
				comparedStart = DateUtils.addDays(on, -60);
				comparedEnd = DateUtils.addDays(on, -30);
			}else if(overDueTimeFrame.intValue()==90){
				comparedStart = DateUtils.addDays(on, -90);
				comparedEnd = DateUtils.addDays(on, -60);
			}
			sb.append(" and dnh.dueDate>=:comparedStart and dnh.dueDate<=:comparedEnd");
		}

		if(sortBy==null){
			sb.append(" order by dnh.demandNoteNo ");
		}else{
			sb.append(" order by dnh."+sortBy);
		}
		Query q = em.createQuery(sb.toString());
		q.setParameter("on", new Date(on.getTime()/* + 24L * 3600 * 1000 -1*/));
		q.setParameter("oustanding", DemandNoteHeader.PAYMENT_STATUS_OUTSTANDING);
		if(overDueTimeFrame!=null){
			q.setParameter("comparedStart", comparedStart);
			q.setParameter("comparedEnd", comparedEnd);
			logger.info("DueDate in {} and {}", new Object[]{comparedStart, comparedEnd});
		}
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findStatusReport(Date from, Date to, Date receiptDateFrom, Date receiptDateTo, String dnStatusParam, String paymentStatus, String sortBy){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT distinct DNH.DEMAND_NOTE_NO, DNH.RM_APPL_NO, DNH.GENERATION_TIME, DNH.DUE_DATE, DNH.DEMAND_NOTE_STATUS, DNH.PAYMENT_STATUS,");
		sb.append(" DNR.INPUT_TIME, DNR.RECEIPT_NO, DNH.BILL_NAME, " );
		sb.append(" DNH.AMOUNT DNH_AMOUNT, DNR.AMOUNT, DNR.REFUND_AMOUNT, DNR.REFUND_PAYMENT_DATE, DNR.PAYMENT_TYPE, DNR.CANCEL_BY, DNH.EBS_FLAG, XX.FORM_CODE   " );
		sb.append(" FROM DEMAND_NOTE_HEADERS DNH left join DEMAND_NOTE_RECEIPTS DNR on DNH.DEMAND_NOTE_NO = DNR.DN_DEMAND_NOTE_NO "
				+ " left join ( select dn_demand_note_no, fc.form_code from DEMAND_NOTE_ITEMS di left outer join FEE_CODES fc on di.FC_FEE_CODE=fc.FEE_CODE  where exists (select 1 from (select dn_demand_note_no, min(item_id) item_id from DEMAND_NOTE_ITEMS group by dn_demand_note_no) x where di.dn_demand_note_no = x.dn_demand_note_no and di.item_id = x.item_id) ) xx     on xx.dn_demand_note_no = dnh.demand_note_no ");
		sb.append(" WHERE   1 = 1 ");
		if (from != null) {
			sb.append(" AND DNH.GENERATION_TIME >=:from ");
		}
		if (to != null) {
			sb.append(" and DNH.GENERATION_TIME <=:to  ");
		}

		if(StringUtils.isNotBlank(dnStatusParam)){
			sb.append(" AND DNH.DEMAND_NOTE_STATUS =:dnStatusParam ");
		}
		if(StringUtils.isNotBlank(paymentStatus)){
			sb.append(" AND DNH.PAYMENT_STATUS =:paymentStatus ");
		}
		if(receiptDateFrom!=null){
			sb.append(" AND DNR.INPUT_TIME >=:receiptDateFrom ");
		}
		if(receiptDateTo!=null){
			sb.append(" AND DNR.INPUT_TIME <=:receiptDateTo ");
		}
		sb.append("ORDER BY DNH.EBS_FLAG, DNH.DEMAND_NOTE_NO ");
		if(StringUtils.isNotBlank(paymentStatus)){
			if(sortBy.equals("0")){
				sb.append(" , DNR.INPUT_TIME");
			}else if(sortBy.equals("1")){
				sb.append(" , DNH.PAYMENT_STATUS");
			}else if(sortBy.equals("3")){
				sb.append(" , DNR.PAYMENT_TYPE");
			}
		}

		Query q = em.createNativeQuery(sb.toString());
		if (from != null) {
			q.setParameter("from", from);
		}
		if (to != null) {
			q.setParameter("to", to);
		}


		if(StringUtils.isNotBlank(dnStatusParam)){
			q.setParameter("dnStatusParam", dnStatusParam);
		}
		if(StringUtils.isNotBlank(paymentStatus)){
			q.setParameter("paymentStatus", paymentStatus);
		}

		if(receiptDateFrom!=null){
			q.setParameter("receiptDateFrom", receiptDateFrom);
		}
		if(receiptDateTo!=null){
			q.setParameter("receiptDateTo", receiptDateTo);
		}

		return q.getResultList();
	}

	@Override
	public DemandNoteHeader findOld(String oldDemandNoteNo){
		try{
			Query query = em.createQuery(" select d from DemandNoteHeader d where d.oldDemandNoteNo =:oldDemandNoteNo ");
			query.setParameter("oldDemandNoteNo", oldDemandNoteNo);
			query.setMaxResults(1);
			return (DemandNoteHeader)query.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteHeader> findCancelled(Date from, Date to, String ebsFlag) {
		List<String> status = new ArrayList<String>();
		status.add(DemandNoteHeader.STATUS_CANCELLED);
		status.add(DemandNoteHeader.STATUS_WRITTEN_OFF);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT dnh from DemandNoteHeader dnh where dnh.status IN :inclList and dnh.cwTime>=:from and dnh.cwTime<=:to ");
		if(ebsFlag==null){
			sb.append(" and dnh.ebsFlag is null ");
		}else if(ebsFlag.equals("1")){
			sb.append(" and dnh.ebsFlag='1' ");
		}else{
			sb.append(" and dnh.ebsFlag is not null and dnh.ebsFlag!='1' ");
		}
		sb.append(" order by dnh.demandNoteNo ");
		Query q = em.createQuery(sb.toString());
		q.setParameter("inclList",status);
		q.setParameter("from", from);
		q.setParameter("to", to);

		return q.getResultList();
	}

}
