package org.mardep.ssrs.dao.dn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.springframework.stereotype.Repository;

@Repository
public class DemandNoteItemJpaDao extends AbstractJpaDao<DemandNoteItem, Long> implements IDemandNoteItemDao {
	public DemandNoteItemJpaDao() {
		for (PredicateCriteria c : criteriaList) {
			switch (c.getKey()) {
			case "applNo":
			case "fcFeeCode":
				criteriaList.set(criteriaList.indexOf(c), new PredicateCriteria(c.getKey(), PredicateType.EQUAL));
				break;
			}
		}
	}
	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<DemandNoteItem> listRoot) {
		listRoot.fetch("feeCode", JoinType.LEFT);
		listRoot.fetch("demandNoteHeader", JoinType.LEFT);
		return criteriaList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteItem> findByDemandNoteNo(String demandNoteNo){
		Query q = em.createQuery("SELECT dni from DemandNoteItem dni left join fetch dni.feeCode dnif where dni.dnDemandNoteNo=:demandNoteNo order by dni.itemId");
		q.setParameter("demandNoteNo", demandNoteNo);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteItem> findSrDnItems() {
		Query query = em.createQuery("select dni from DemandNoteItem dni where dni.applNo != '0' and dni.active = true ");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteItem> findUnusedByApplNo(String applNo) {
		Query query = em.createQuery("select dni from DemandNoteItem dni where (:applNo is null or dni.applNo = :applNo) and dni.dnDemandNoteNo is null and dni.active = true");
		query.setParameter("applNo", applNo);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getOutstandingDn(List<String> imoNoList) {
		String jpel = "select rm.applNo, rm.regName, rm.regChiName, dni.generationTime, rm.atfDueDate, dni.amount, dni.itemId, rm.regNetTon, rm.imoNo "
				+ " from DemandNoteItem dni , RegMaster rm "
				+ "where dni.applNo = rm.applNo and dni.fcFeeCode = '01' and dni.dnDemandNoteNo is null "
				+ "and rm.atfDueDate between :todayNextYear and :tmrNextYear ";

		if (imoNoList == null) {
			jpel += "and (rm.deRegTime is null or rm.deRegTime between :today and :today2359 )";
		} else {
			jpel += "and rm.imoNo in (:imoNoList)";
		}
		Query query = em.createQuery(jpel);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

		try {
			Date nextYr = format.parse(String.valueOf(Long.parseLong(format.format(new Date())) + 10000));
			query.setParameter("todayNextYear", nextYr);
			query.setParameter("tmrNextYear", new Date(nextYr.getTime() + 24L * 3600*1000));
			if (imoNoList == null) {
				Date today = format.parse(format.format(new Date()));
				query.setParameter("today", today);
				query.setParameter("today2359", new Date(today.getTime() + 24L * 3600*1000 - 1000));
			} else {
				query.setParameter("imoNoList", imoNoList);
			}
		} catch (ParseException e) {
			throw new RuntimeException("cannot get next year");
		}
		return query.getResultList();

	}

	@Override
	public DemandNoteItem getLastAtc(String applNo) {
		try {
			String sql = "select dni from DemandNoteItem dni where dni.dnDemandNoteNo is not null and dni.fcFeeCode = '01' and dni.applNo = :applNo order by dni.generationTime desc";
			Query query = em.createQuery(sql, DemandNoteItem.class)
					.setParameter("applNo", applNo);
			List<DemandNoteItem> itemList = (List<DemandNoteItem>)query.getResultList();
			if (itemList.size()>0) {
				if (itemList.get(0)!=null) {
					return itemList.get(0);
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		}
	}
	
	@Override
	public List<Map<String, Object>> outstandingReport(Date start, Date end) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select appl_no applNo, reg_name shipNameEng, reg_cname shipNameChi, eng_desc descEng, chn_desc descChi, generation_time issueTime, amount, dni.user_id userId "
				+ "from demand_note_items dni inner join fee_codes fc on dni.fc_fee_code = fc.fee_code\r\n" + 
				"inner join reg_masters rm on dni.rm_appl_no = rm.appl_no\r\n" + 
				"where dn_demand_note_no is null and generation_time between :start and :end "
				+ " and dni.delete_reason is null and dni.active = 1 "
				+ " order by dni.generation_time asc";
		Query query = em.createNativeQuery(sql);
		query.setParameter("start", start);
		query.setParameter("end", end);
		
		query.getResultList().forEach(r -> {
			Map<String, Object> row = new HashMap<>();
			Object[] array = (Object[]) r;
			row.put("applNo", array[0]);
			row.put("shipNameEng", array[1]);
			row.put("shipNameChi", array[2]);
			row.put("descEng", array[3]);
			row.put("descChi", array[4]);
			row.put("issueTime", array[5]);
			row.put("amount", array[6]);
			row.put("userId", array[7]);
			list.add(row);
		});
		return list;
	}
}
