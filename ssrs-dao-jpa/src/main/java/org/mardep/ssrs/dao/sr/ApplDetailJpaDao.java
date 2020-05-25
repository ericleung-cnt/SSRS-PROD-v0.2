package org.mardep.ssrs.dao.sr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.print.attribute.HashAttributeSet;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.springframework.stereotype.Repository;

@Repository
public class ApplDetailJpaDao extends AbstractJpaDao<ApplDetail, String> implements IApplDetailDao {
	private List<PredicateCriteria> criteriaList = new ArrayList<>();

	public ApplDetailJpaDao() {
		criteriaList.add(new PredicateCriteria("applNo", PredicateType.EQUAL));
	}
	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb, Root<ApplDetail> listRoot) {
		return criteriaList;
	}
	@Override
	public List<Map<String, Object>> deregReasonsReport(Date start, Date end) {
		Query query = em.createNativeQuery("select appl_no, reg_name, dereg_time, client_dereg_reason, client_dereg_remark from reg_masters rm \r\n" + 
				"inner join appl_details ad on ad.rm_appl_no = rm.appl_no \r\n" + 
				"where format(dereg_time, 'yyyy-MM-dd') between :start and :end"
				+ " and client_dereg_reason is not null \r\n" + 
				"order by dereg_time ");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		query.setParameter("start", df.format(start));
		query.setParameter("end", df.format(end));
		List<Map<String, Object>> list = new ArrayList<>();
		query.getResultList().forEach(r -> {
			Object[] array = (Object[]) r;
			Map<String, Object> row = new HashMap<>();
			row.put("applNo", array[0]);
			row.put("shipNameEng", array[1]);
			row.put("deregTime", array[2]);
			row.put("reason", array[3]);
			row.put("remark", array[4]);
			list.add(row);
		});
		
		return list;
	}
}
